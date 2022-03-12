package app.services.scene

import app.components.AgentInterface
import app.components.configuration.Configuration
import app.components.configuration.MutableRequestSignature
import app.components.experiment.Experiment
import app.services.Service
import app.services.user.Scene
import app.utils.runBlockCatching
import core.api.dto.*
import core.components.Component
import core.components.Script
import core.components.configuration.AgentInterfaces
import core.components.configuration.GlobalArgsComponent
import core.components.getComponent
import core.entities.*
import org.reflections.Reflections
import org.reflections.util.ConfigurationBuilder
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance

interface SceneApi {
    fun getGlobalArgs(): GlobalArgs
    fun updateWith(snapshot: Snapshot)
    fun getBehaviour(): Behaviour
    fun onModelRun()
    fun onModelStop()
    fun onModelPause()
    fun onModelResume()
}

class SceneService : Service(), SceneApi {
    val scene: Scene
        get() = _scene

    private val _scene: SceneImpl = SceneFactory.createScene()

    override fun getBehaviour(): Behaviour {
        val agentBehaviourRequests = scene.agents.entries
            .asSequence()
            .map { (id, agent) ->
                val agentInterface = agent.getAgentInterfaceScript()
                val requests = agentInterface.commitRequests()
                id to requests
            }.filterNot { (_, requests) -> requests.isEmpty() }.map { (id, requests) ->
                AgentBehaviour(id, requests)
            }.toList()
        return Behaviour(agentBehaviourRequests)
    }

    override fun getGlobalArgs(): GlobalArgs {
        return GlobalArgs(scene.environment.getComponent<GlobalArgsComponent>()!!.globalArgs)
    }

    override fun updateWith(snapshot: Snapshot) {
        updateAgents(snapshot.agentSnapshots)
        updateScripts(snapshot.time)
        onAfterModelUpdate()
    }

    private fun updateAgents(agentSnapshots: List<AgentSnapshot>) {
        val deadAgentIds = scene.agents.keys.toMutableSet()
        for (agentSnapshot in agentSnapshots) {
            runBlockCatching {
                val agentInterfacesComponent = scene.environment.getComponent<AgentInterfaces>()!!
                updateAgentWithSnapshot(agentSnapshot, agentInterfacesComponent)
            }
            deadAgentIds.remove(agentSnapshot.id)
        }
        deadAgentIds.forEach { _scene.removeAgentById(it) }
    }

    private fun updateAgentWithSnapshot(agentSnapshot: AgentSnapshot, agentInterfacesComponent: AgentInterfaces) {
        if (agentSnapshot.id in scene.agents.keys) {
            val oldAgent = scene.agents[agentSnapshot.id]!!
            oldAgent.updateSnapshot(agentSnapshot, agentInterfacesComponent)
        } else {
            val agentInterface = agentInterfacesComponent.agentInterfaces[agentSnapshot.type]
            val newAgent = EntityFactory.createAgent(
                agentSnapshot.type,
                agentInterface?.setters?.toList() ?: listOf(),
                agentInterface?.otherRequests?.toList() ?: listOf()
            )
            newAgent.updateSnapshot(agentSnapshot, agentInterfacesComponent)
            _scene.addAgent(agentSnapshot.id, newAgent)
        }
    }

    private fun Agent.updateSnapshot(snapshot: AgentSnapshot, agentInterfacesComponent: AgentInterfaces) {
        val properties = snapshot.props
        val configuredProperties = mutableMapOf<String, Any>()
        val propertyConfiguration = agentInterfacesComponent.agentInterfaces[snapshot.type]?.properties?.forEach {
            if (it in properties) {
                configuredProperties[it] = properties[it]!!
            }
        }
        val configuredSnapshot = when (propertyConfiguration) {
            null -> snapshot
            else -> snapshot.copy(props = configuredProperties)
        }
        getAgentInterfaceScript().snapshot = configuredSnapshot
    }

    private fun Agent.getAgentInterfaceScript() = getComponent<AgentInterface>()!!

    private fun getScripts(): Sequence<Script> =
        _scene.getEntities().asSequence()
            .flatMap { it.getComponents() }
            .filterIsInstance<Script>()

    private fun onAfterModelUpdate() = forEachScript(Script::onModelAfterUpdate)
    private fun updateScripts(modelTime: Float) = forEachScript { onModelUpdate(modelTime) }
    override fun onModelRun() = forEachScript(Script::onModelRun)
    override fun onModelStop() = forEachScript(Script::onModelStop)
    override fun onModelPause() = forEachScript(Script::onModelPause)
    override fun onModelResume() = forEachScript(Script::onModelResume)
    fun updateScriptsUI() = forEachScript(Script::updateUI)

    private fun forEachScript(block: Script.() -> Unit) {
        getScripts().forEach {
            runBlockCatching {
                it.block()
            }
        }
    }

    private fun clearScene() {
        _scene.apply {
            agents.map { it.key }.forEach { removeAgentById(it) }
            environment.getComponents().forEach {
                environment.removeComponent(it::class)
            }
            experimenter.getComponents().forEach {
                experimenter.removeComponent(it::class)
            }
        }
    }
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
        val experimenter = Experimenter()
        experimenter.setComponent<Experiment>()
        return experimenter
    }

    fun createEnvironment(): Environment {
        return Environment().apply {
            setComponent<Configuration>()
        }
    }

    fun createAgent(
        agentType: String,
        setterSignatures: List<MutableRequestSignature>,
        otherRequestSignatures: List<MutableRequestSignature>
    ): Agent {
        return Agent(agentType).apply {
            with(setComponent<AgentInterface>()) {
                setRequestSignatures(setterSignatures + otherRequestSignatures)
            }
        }
    }
}