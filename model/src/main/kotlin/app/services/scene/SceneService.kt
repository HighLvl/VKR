package app.services.scene

import app.components.AgentInterface
import app.components.experiment.Experiment
import app.services.EventBus
import app.services.GlobalArgsSet
import app.services.Service
import app.services.listen
import app.services.model.configuration.ModelConfiguration
import app.services.model.configuration.MutableModelConfiguration
import app.services.model.configuration.MutableRequestSignature
import app.services.user.Scene
import app.utils.KtsScriptEngine
import app.utils.runBlockCatching
import core.api.dto.*
import core.components.Component
import core.components.Script
import core.components.getComponent
import core.entities.*
import core.services.logger.Level
import core.services.logger.Logger
import org.reflections.Reflections
import org.reflections.util.ConfigurationBuilder
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance

interface SceneApi {
    fun updateSceneWith(snapshot: Snapshot)
    fun getBehaviour(): Behaviour
    fun onModelRun()
    fun onModelStop()
    fun onModelPause()
    fun onModelResume()
}

class SceneService : Service(), SceneApi {
    val scene: Scene
        get() = _scene
    var configuration: ModelConfiguration = MutableModelConfiguration()
        private set
    val globalArgs = EventBus.listen<GlobalArgsSet>().map { it.args.args }

    private val _globalArgs = (mapOf<String, Any>())
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

    override fun updateSceneWith(snapshot: Snapshot) {
        updateAgents(snapshot.agentSnapshots)
        updateScripts(snapshot.time)
        onAfterModelUpdate()
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
                agentInterface?.otherRequests?.toList() ?: listOf()
            )
            newAgent.updateSnapshot(agentSnapshot)
            _scene.addAgent(agentSnapshot.id, newAgent)
        }
    }

    private fun Agent.updateSnapshot(snapshot: AgentSnapshot) {
        val properties = snapshot.props
        val configuredProperties = mutableMapOf<String, Any>()
        val propertyConfiguration = configuration.agentInterfaces[snapshot.type]?.properties?.forEach {
            if (it in properties) {
                configuredProperties[it] = properties[it]!!
            }
        }
        val configuredSnapshot = when {
            propertyConfiguration == null -> snapshot
            else -> snapshot.copy(props = configuredProperties)
        }
        getAgentInterfaceScript().snapshot = configuredSnapshot
    }

    private fun Agent.getAgentInterfaceScript() = getComponent<AgentInterface>()!!

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

    override fun onModelRun() {
        getScripts().forEach {
            runBlockCatching {
                it.onModelRun()
            }
        }
    }

    override fun onModelStop() {
        getScripts().forEach {
            runBlockCatching {
                it.onModelStop()
            }
        }
    }

    override fun onModelPause() {
        getScripts().forEach {
            runBlockCatching {
                //TODO onModelPause
            }
        }
    }

    override fun onModelResume() {
        getScripts().forEach {
            runBlockCatching {
                //TODO onModelResume
            }
        }
    }

    fun updateScriptsUI() {
        getScripts().forEach {
            runBlockCatching {
                it.updateUI()
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
            Logger.log("Configuration loaded\n $configuration", Level.INFO)
        } catch (e: Exception) {
            Logger.log("Bad configuration file", Level.ERROR)
        }
    }

    private fun loadConfigurationByPath(path: String) {
        configuration = KtsScriptEngine.eval(path)
        setGlobalArgs(configuration.globalArgs)
        Logger.log("Global args\n${configuration.globalArgs}", Level.DEBUG)
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

    private val componentTree = mutableMapOf<Int, KClass<out Component>>()

    fun addComponent(entity: Entity, id: Int) {
        entity.setComponent(componentTree[id]!!)
    }

    fun getComponentTree(): Map<Int, Node> {
        val components = Reflections(
            ConfigurationBuilder().forPackages(
                "app.components",
                "user"
            )
        ).getSubTypesOf(Component::class.java)
            .asSequence()
            .filter { !it.kotlin.isAbstract && kotlin.runCatching { it.kotlin.createInstance() }.isSuccess }
            .map { it.kotlin }.filterNot { it in listOf(AgentInterface::class, Experiment::class) }.toList()
        val userComponents = components.filter { it.qualifiedName.toString().startsWith("user.") }
        val appComponents = components.filter { it.qualifiedName.toString().startsWith("app.") }
        val userNodes = mutableListOf<Int>()
        val tree = mutableMapOf<Int, Node>(
            0 to FolderNode("Components", listOf(1)),
            1 to FolderNode("User Components", userNodes)
        )
        for (i in userComponents.indices) {
            val id = i + 2
            userNodes.add(id)
            tree[id] = ComponentNode(userComponents[i].qualifiedName.toString())
            componentTree[id] = userComponents[i]
        }
        return tree
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
        val environment = Environment()
        return environment
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

sealed interface Node {
    val name: String
}

data class ComponentNode(override val name: String) : Node
data class FolderNode(override val name: String, val nodes: List<Int>) : Node