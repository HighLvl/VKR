package app.services.scene

import app.components.AgentInterfaceImpl
import app.components.experiment.Experiment
import app.logger.Log
import app.logger.Logger
import app.services.user.Scene
import app.services.model.configuration.ModelConfiguration
import app.services.model.configuration.MutableModelConfiguration
import app.services.model.configuration.MutableRequestSignature
import app.utils.KtsScriptEngine
import app.utils.runBlockCatching
import core.api.dto.*
import core.components.Script
import core.components.getComponent
import core.entities.Agent
import core.entities.Environment
import core.entities.Experimenter
import core.services.*
import io.reactivex.rxjava3.disposables.Disposable

class SceneService : Service() {
    val scene: Scene
        get() = _scene
    var configuration: ModelConfiguration = MutableModelConfiguration()
        private set
    val globalArgs = EventBus.listen<GlobalArgsSet>().map { it.args.args }

    private val _globalArgs = (mapOf<String, Any>())
    private val _scene: SceneImpl = SceneFactory.createScene()
    private val disposables = mutableListOf<Disposable>()

    override fun start() {
        super.start()
        disposables += EventBus.listen<SnapshotReceive>().subscribe {
            updateAgentModel(it.snapshot)
        }
        disposables += EventBus.listen<AgentModelLifecycleEvent.Run>().subscribe {
            onModelRun()
        }
        disposables += EventBus.listen<AgentModelLifecycleEvent.Stop>().subscribe {
            onModelStop()
        }
        disposables += EventBus.listen<Update>().subscribe {
            updateScripts()
        }
    }

    override fun stop() {
        super.stop()
        disposables.forEach { it.dispose() }
        disposables.clear()
    }

    private fun updateAgentModel(snapshot: Snapshot) {
        updateAgents(snapshot.agentSnapshots)
        updateScripts(snapshot.time)
        sendBehaviour()
        onAfterModelUpdate()
    }

    private fun sendBehaviour() {
        val agentBehaviourRequests = scene.agents.entries
            .asSequence()
            .map { (id, agent) ->
                val agentInterface = agent.getAgentInterfaceScript()
                val requests = agentInterface.commitRequests()
                id to requests
            }.filterNot { (_, requests) -> requests.isEmpty() }.map { (id, requests) ->
                AgentBehaviour(id, requests)
            }.toList()
        if (agentBehaviourRequests.isEmpty()) return
        EventBus.publish(BehaviourRequestsReady(Behaviour(agentBehaviourRequests)))
    }

    private fun updateAgents(agentSnapshots: List<AgentSnapshot>) {
        val deadAgentIds = _scene.agents.keys.toMutableSet()
        for (agentSnapshot in agentSnapshots) {
            runBlockCatching { updateAgentWithSnapshot(agentSnapshot) }
            deadAgentIds.remove(agentSnapshot.id)
        }
        deadAgentIds.forEach { _scene.removeAgentById(it) }
    }

    private fun updateAgentWithSnapshot(agentSnapshot: AgentSnapshot) {
        if (agentSnapshot.id in _scene.agents.keys) {
            val oldAgent = _scene.agents[agentSnapshot.id]!!
            oldAgent.updateSnapshot(agentSnapshot)
        } else {
            val agentInterface = configuration.agentInterfaces[agentSnapshot.type]
            val newAgent = EntityFactory.createAgent(
                agentSnapshot.type,
                agentInterface?.setters?.toList() ?: listOf(),
                agentInterface?.requestSignatures?.toList() ?: listOf()
            )
            newAgent.updateSnapshot(agentSnapshot)
            _scene.addAgent(agentSnapshot.id, newAgent)
        }
    }

    private fun Agent.updateSnapshot(snapshot: AgentSnapshot) {
        getAgentInterfaceScript().snapshot = snapshot
    }

    private fun Agent.getAgentInterfaceScript() = getComponent<AgentInterfaceImpl>()!!

    private fun updateScripts(modelTime: Float) {
        getScripts().forEach {
            runBlockCatching {
                it.onModelUpdate(modelTime)
            }
        }
    }

    private fun getScripts(): List<Script> =
        _scene.getEntities().asSequence()
            .flatMap { it.getComponents() }
            .filterIsInstance<Script>()
            .toList()

    private fun onAfterModelUpdate() {
        getScripts().forEach {
            runBlockCatching {
                it.onModelAfterUpdate()
            }
        }
    }

    private fun onModelRun() {
        getScripts().forEach {
            runBlockCatching {
                it.onModelRun()
            }
        }
    }

    private fun onModelStop() {
        getScripts().forEach {
            runBlockCatching {
                it.onModelStop()
            }
        }
    }

    private fun updateScripts() {
        getScripts().forEach {
            runBlockCatching {
                it.update()
            }
        }
    }

    fun setGlobalArgs(args: Map<String, Any>) {
        EventBus.publish(GlobalArgsSet(GlobalArgs(args)))
    }

    fun loadConfiguration(path: String) {
        if (path.isEmpty()) return
        try {
            loadConfigurationByPath(path)
            Logger.log("Configuration loaded\n $configuration", Log.Level.INFO)
        } catch (e: Exception) {
            Logger.log("Bad configuration file", Log.Level.ERROR)
        }
    }

    private fun loadConfigurationByPath(path: String) {
        configuration = KtsScriptEngine.eval(path)
        setGlobalArgs(configuration.globalArgs)
        Logger.log("Global args\n${configuration.globalArgs}", Log.Level.DEBUG)
        clearScene()
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
        experimenter.setComponent(Experiment())
        return experimenter
    }

    fun createEnvironment(): Environment {
        val environment = Environment()
        return environment
    }

    fun createAgent(
        agentType: String,
        setterSignatures: List<MutableRequestSignature>,
        otherRequestSignatures: List<MutableRequestSignature>
    ): Agent {
        val agentInterface = AgentInterfaceImpl(setterSignatures, otherRequestSignatures)
        val agent = Agent(agentType).apply {
            setComponent(agentInterface)
        }
        return agent
    }
}