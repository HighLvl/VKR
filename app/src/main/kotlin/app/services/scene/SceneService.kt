package app.services.scene

import app.api.dto.AgentSnapshot
import app.api.dto.Error
import app.api.dto.ModelInputArgs
import app.api.dto.Request
import app.api.dto.Snapshot
import app.components.agent.AgentInterface
import app.components.base.SystemComponent
import app.components.configuration.AgentInterfaces
import app.components.configuration.Configuration
import app.components.experiment.Experiment
import app.requests.RequestIO
import app.requests.RequestSender
import app.requests.Response
import app.services.Service
import core.components.base.Component
import core.components.base.Script
import core.components.configuration.InputArgsComponent
import core.components.configuration.MutableRequestSignature
import core.entities.*
import core.services.logger.Level
import core.services.logger.Logger
import core.services.scene.Scene
import core.utils.runBlockCatching
import kotlinx.coroutines.runBlocking
import kotlin.reflect.KClass
import kotlin.reflect.full.isSubclassOf

interface SceneApi {
    fun getInputArgs(): ModelInputArgs
    fun updateWith(snapshot: Snapshot)
    fun getRequests(): List<Request>
    fun onModelRun()
    fun onModelStop()
    fun onModelPause()
    fun onModelResume()
}

class SceneService(private val requestIO: RequestIO, private val requestSender: RequestSender) : Service(), SceneApi {
    val scene: Scene
        get() = _scene

    private val _scene: SceneImpl = SceneFactory.createScene()
    private var prevTime: Double? = null

    override fun getRequests(): List<Request> {
        return requestIO.commitRequests().map { Request(it.agentId, it.name, it.ack, it.args) }
    }

    override fun getInputArgs(): ModelInputArgs {
        return ModelInputArgs(scene.environment.getComponent<InputArgsComponent>()!!.inputArgs)
    }

    override fun updateWith(snapshot: Snapshot) {
        handleResponses(snapshot.responses)
        if (snapshot.t == prevTime) return
        prevTime = snapshot.t
        updateAgents(snapshot.agentSnapshots)
        updateScripts(snapshot.t)
        onAfterModelUpdate()
    }

    private fun handleResponses(responses: List<app.api.dto.Response>) {
        requestIO.handleResponses(responses.map {
            val result = when (it.success) {
                true -> Result.success(it.value)
                false -> {
                    val error = it.value as Error
                    Logger.log(
                        "An error occurred in the model\ncode: ${error.code}, message: \"${error.text}\"",
                        Level.ERROR
                    )
                    Result.failure(kotlin.runCatching { throw error }.exceptionOrNull()!!)
                }
            }
            Response(it.ack, result)
        })
    }

    private fun updateAgents(agentSnapshots: Map<String, List<AgentSnapshot>>) {
        val deadAgentIds = scene.agents.keys.toMutableSet()
        for ((type, snap) in agentSnapshots) {
            for (agentSnapshot in snap) {
                runBlockCatching {
                    val agentInterfacesComponent = scene.environment.getComponent<AgentInterfaces>()!!
                    updateAgentWithSnapshot(type, agentSnapshot, agentInterfacesComponent)
                }
                deadAgentIds.remove(agentSnapshot.id)
            }
        }

        deadAgentIds.forEach { _scene.removeAgentById(it) }
    }

    private fun updateAgentWithSnapshot(
        type: String,
        agentSnapshot: AgentSnapshot,
        agentInterfacesComponent: AgentInterfaces
    ) {
        if (agentSnapshot.id in scene.agents.keys) {
            val oldAgent = scene.agents[agentSnapshot.id]!!
            oldAgent.updateSnapshot(type, agentSnapshot, agentInterfacesComponent)
        } else {
            val agentInterface = agentInterfacesComponent.agentInterfaces[type]
            val newAgent = EntityFactory.createAgent(
                type,
                agentInterface?.setters?.toList() ?: listOf(),
                agentInterface?.otherRequests?.toList() ?: listOf(),
                requestSender
            )
            newAgent.updateSnapshot(type, agentSnapshot, agentInterfacesComponent)
            _scene.addAgent(agentSnapshot.id, newAgent)
        }
    }

    private fun Agent.updateSnapshot(
        type: String,
        agentSnapshot: AgentSnapshot,
        agentInterfacesComponent: AgentInterfaces
    ) {
        val properties = agentSnapshot.props
        val configuredProperties = mutableMapOf<String, Any>()
        val propertyConfiguration = agentInterfacesComponent.agentInterfaces[type]?.properties?.forEach {
            if (it.name in properties.keys) {
                configuredProperties[it.name] = properties[it.name]!!
            }
        }
        val configuredSnapshot = when (propertyConfiguration) {
            null -> agentSnapshot
            else -> agentSnapshot.copy(props = configuredProperties)
        }
        getAgentInterfaceScript().snapshot = configuredSnapshot
    }

    private fun Agent.getAgentInterfaceScript() = getComponent<AgentInterface>()!!

    private fun getScripts(): Sequence<Script> =
        _scene.getEntities().asSequence()
            .flatMap { it.getComponents() }
            .filterIsInstance<Script>()

    private fun onAfterModelUpdate() = forEachScript(Script::onModelAfterUpdate)
    private fun updateScripts(modelTime: Double) = forEachScript { onModelUpdate(modelTime) }
    override fun onModelRun() {
        _scene.apply { agents.map { it.key }.forEach { removeAgentById(it) } }
        forEachScript(Script::onModelRun)
    }

    override fun onModelStop() = forEachScript(Script::onModelStop)
    override fun onModelPause() = forEachScript(Script::onModelPause)
    override fun onModelResume() = forEachScript(Script::onModelResume)
    fun updateScriptsUI() {
        runBlocking { forEachScript(Script::updateUI) }
    }

    private fun forEachScript(block: Script.() -> Unit) {
        getScripts().forEach {
            runBlockCatching {
                it.block()
            }
        }
    }
//
//    private fun clearScene() {
//        _scene.apply {
//            agents.map { it.key }.forEach { removeAgentById(it) }
//            environment.getComponents().forEach {
//                environment.removeComponent(it::class)
//            }
//            experimenter.getComponents().forEach {
//                experimenter.removeComponent(it::class)
//            }
//        }
//    }
}

object SceneFactory {
    fun createScene(): SceneImpl {
        val scene = SceneImpl().apply {
            setEnvironment(EntityFactory.createEnvironment())
            setExperimenter(EntityFactory.createExperimenter())
        }
        return scene
    }
}

object EntityFactory {
    fun createExperimenter(): Experimenter {
        val experimenter = Experimenter(SystemComponentHolder())
        experimenter.setComponent<Experiment>()
        return experimenter
    }

    fun createEnvironment(): Environment {
        return Environment(SystemComponentHolder()).apply {
            setComponent<Configuration>()
        }
    }

    fun createAgent(
        agentType: String,
        setterSignatures: List<MutableRequestSignature>,
        otherRequestSignatures: List<MutableRequestSignature>,
        requestSender: RequestSender
    ): Agent {
        return Agent(agentType, SystemComponentHolder()).apply {
            with(setComponent<AgentInterface>()) {
                setRequestSignatures(setterSignatures + otherRequestSignatures)
                setRequestSender(requestSender)
            }
        }
    }

    private class SystemComponentHolder(private val componentHolder: ComponentHolder = MapComponentHolder()) :
        ComponentHolder by componentHolder {
        override fun <C : Component> removeComponent(type: KClass<C>): C? {
            if (type.isSubclassOf(SystemComponent::class)) return null
            return componentHolder.removeComponent(type)
        }
    }
}