package app.services.scene

import app.api.dto.AgentSnapshot
import app.api.dto.ModelInputArgs
import app.api.dto.Snapshot
import app.components.system.agent.AgentInterface
import app.components.system.configuration.AgentInterfaces
import app.components.system.model.SnapshotInfo
import app.requests.RequestSender
import app.services.Service
import app.services.scene.SceneService.Companion.updateUI
import app.services.scene.factory.EntityFactory
import app.services.scene.factory.SceneFactory
import app.utils.getAccessibleFunction
import core.components.base.Script
import core.components.configuration.InputArgsComponent
import core.coroutines.Contexts
import core.entities.Agent
import core.entities.getComponent
import core.services.scene.Scene
import core.utils.runBlockCatching
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class SceneService(private val requestSender: RequestSender) : Service(), SceneApi {
    val scene: Scene
        get() = _scene

    private val _scene: SceneImpl = SceneFactory.createScene()

    private var prevTime = Double.MIN_VALUE
    private val coroutineScope = CoroutineScope(Contexts.app)

    init {
        coroutineScope.launch {
            _scene.environment.getComponent<AgentInterfaces>()!!.agentInterfaces.collect {
                val oldAgentPrototypeTypes = _scene.agentPrototypes.keys.toSet()
                oldAgentPrototypeTypes.forEach { agentType ->
                    if (agentType !in it) {
                        _scene.removeAgentPrototype(agentType)
                    }
                }
                it.keys.forEach { agentType ->
                    if (agentType !in oldAgentPrototypeTypes) {
                        _scene.putAgentPrototype(agentType, EntityFactory.createAgentPrototype(agentType))
                    }
                }
            }
        }
    }

    override fun getInputArgs(): ModelInputArgs {
        return ModelInputArgs(scene.environment.getComponent<InputArgsComponent>()!!.inputArgs)
    }

    override fun updateWith(snapshot: Snapshot) {
        if (prevTime == snapshot.t) return
        prevTime = snapshot.t
        updateSnapshotInfo(snapshot.t)
        updateAgents(snapshot.agentSnapshots)
        updateScripts()
        onAfterModelUpdate()
    }

    private fun updateSnapshotInfo(t: Double) {
        with(scene.environment.getComponent<SnapshotInfo>()!!) {
            dt = t - modelTime
            modelTime = t
        }
    }

    private fun updateAgents(agentSnapshots: Map<String, List<AgentSnapshot>>) {
        val deadAgentIds = scene.agents.keys.toMutableSet()
        for ((type, snap) in agentSnapshots) {
            for (agentSnapshot in snap) {
                val agentInterfacesComponent = scene.environment.getComponent<AgentInterfaces>()!!
                updateAgentWithSnapshot(type, agentSnapshot, agentInterfacesComponent)
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
            val agentPrototype = _scene.getAgentPrototype(type) ?: return
            val agentInterface = agentInterfacesComponent.agentInterfaces.value[type]

            val newAgent = EntityFactory.createAgent(
                agentPrototype,
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
        val propertyConfiguration = agentInterfacesComponent.agentInterfaces.value[type]?.properties?.forEach {
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

    private fun onAfterModelUpdate() = forEachScript { onModelAfterUpdate.call(it) }
    private fun updateScripts() = forEachScript { onModelUpdate.call(it)}
    override fun onModelRun() {
        prevTime = Double.MIN_VALUE
        resetSnapshotInfo()
        _scene.apply { agents.map { it.key }.forEach { removeAgentById(it) } }
        forEachScript { onModelRun.call(it) }
    }

    private fun resetSnapshotInfo() {
        with(scene.environment.getComponent<SnapshotInfo>()!!) {
            dt = 0.0
            modelTime = 0.0
        }
    }

    override fun onModelStop() = forEachScript { onModelStop.call(it) }
    override fun onModelPause() = forEachScript { onModelPause.call(it) }
    override fun onModelResume() = forEachScript { onModelResume.call(it) }
    fun updateScriptsUI() {
        forEachScript { updateUI.call(it)}
    }

    private fun forEachScript(block: (Script) -> Unit) {
        getScripts().forEach {
            runBlockCatching {
                block(it)
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

    private companion object {
        val onModelRun = getAccessibleFunction<Script>("onModelRun")
        val onModelUpdate = getAccessibleFunction<Script>("onModelUpdate")
        val onModelAfterUpdate = getAccessibleFunction<Script>("onModelAfterUpdate")
        val onModelStop = getAccessibleFunction<Script>("onModelStop")
        val updateUI = getAccessibleFunction<Script>("updateUI")
        val onModelPause = getAccessibleFunction<Script>("onModelPause")
        val onModelResume = getAccessibleFunction<Script>("onModelResume")
    }
}

