package app.services.scene

import core.entities.Agent
import core.entities.Environment
import core.entities.Experimenter
import core.scene.Scene
import app.components.AgentInterface
import app.components.experiment.Experiment
import app.logger.Log
import app.logger.Logger
import app.scene.SceneImpl
import app.services.model.configuration.ModelConfiguration
import app.utils.KtsScriptEngine
import core.components.getComponent
import core.services.*
import core.api.dto.AgentSnapshot
import core.api.dto.GlobalArgs
import core.api.dto.Snapshot

class SceneService : Service() {
    private val _scene: SceneImpl = SceneFactory.createScene()
    val scene: Scene = _scene
    var configuration: ModelConfiguration = ModelConfiguration()
    private set

    private val _globalArgs = (mapOf<String, Any>())
    val globalArgs = EventBus.listen<AgentModelLifecycleEvent.GlobalArgsSet>().map { it.args.args }

    override fun start() {
        updateAgentModelOnSnapshotReceive()
    }

    private fun updateAgentModelOnSnapshotReceive() {
        EventBus.listen<SnapshotReceive>().subscribe {
            updateAgentModel(it.snapshot)
        }
    }

    private fun updateAgentModel(snapshot: Snapshot) {
        updateAgents(snapshot.agentSnapshots)
        onUpdate(snapshot.time)
        onAfterUpdate()
    }

    private fun updateAgents(agentSnapshots: List<AgentSnapshot>) {
        val deadAgentIds = _scene.agents.keys.toMutableSet()
        for (agentSnapshot in agentSnapshots) {
            updateAgentWithSnapshot(agentSnapshot)
            deadAgentIds.remove(agentSnapshot.id)
        }
        deadAgentIds.forEach { _scene.removeAgentById(it) }
    }

    private fun updateAgentWithSnapshot(agentSnapshot: AgentSnapshot) {
        if (agentSnapshot.id in _scene.agents.keys) {
            val oldAgent = _scene.agents[agentSnapshot.id]!!
            oldAgent.updateSnapshot(agentSnapshot)
        } else {
            val newAgent = EntityFactory.createAgent()
            newAgent.updateSnapshot(agentSnapshot)
            _scene.addAgent(agentSnapshot.id, newAgent)
        }
    }

    private fun Agent.updateSnapshot(snapshot: AgentSnapshot) {
        getAgentInterfaceScript().snapshot = snapshot
    }

    private fun Agent.getAgentInterfaceScript() = getComponent<AgentInterface>()!!

    private fun onUpdate(modelTime: Float) {
        EventBus.publish(AgentModelLifecycleEvent.Update(modelTime))
    }

    private fun onAfterUpdate() {
        EventBus.publish(AgentModelLifecycleEvent.AfterUpdate)
    }

    fun setGlobalArgs(args: Map<String, Any>) {
        EventBus.publish(AgentModelLifecycleEvent.GlobalArgsSet(GlobalArgs(args)))
    }

    fun loadConfiguration(path: String) {
        if (path.isEmpty()) return
        try {
            _loadConfiguration(path)
            Logger.log("Configuration loaded\n $configuration", Log.Level.INFO)
        } catch (e: Exception) {
            Logger.log("Bad configuration file", Log.Level.ERROR)
        }
    }

    private fun _loadConfiguration(path: String) {
        configuration = KtsScriptEngine.eval(path)
        setGlobalArgs(configuration.globalArgs)
        Logger.log("Global args\n${configuration.globalArgs}", Log.Level.DEBUG)

    }

    fun clearScene() {
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
        experimenter.setComponent(Experiment())
        return experimenter
    }

    fun createEnvironment(): Environment {
        val environment = Environment()
        return environment
    }

    fun createAgent(): Agent {
        val agentInterface = AgentInterface(mutableListOf(), mutableListOf())
        val agent = Agent("Simple agent").apply {
            setComponent(agentInterface)
        }
        return agent
    }
}