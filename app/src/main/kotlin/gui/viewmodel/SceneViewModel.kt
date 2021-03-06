package gui.viewmodel

import app.services.scene.SceneService
import com.fasterxml.jackson.databind.node.ObjectNode
import core.coroutines.Contexts
import core.gui.EventBus
import core.gui.UIEvent
import core.utils.splitOnCapitalChars
import gui.utils.getString
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.channels.ticker
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

@OptIn(ObsoleteCoroutinesApi::class)
class SceneViewModel(
    private val sceneService: SceneService
) :
    ViewModel() {
    private val _components = MutableStateFlow<List<ComponentDto>>(listOf())
    private val _selectedEntity = MutableStateFlow<Entity>(None)
    private val _objectTree = MutableStateFlow(Folder(ROOT_FOLDER_TITLE, listOf()))
    private val _availableComponents = MutableStateFlow(emptyMap<Int, app.services.repository.component.Node>())
    private val componentManager = ComponentManager()

    val components = _components.asStateFlow()
    val selectedEntity = _selectedEntity.asStateFlow()
    val objectTree = _objectTree.asStateFlow()
    val availableComponents = _availableComponents.asStateFlow()

    init {
        launchWithAppContext {
            ticker(REFRESH_DELAY_MS, REFRESH_DELAY_MS, coroutineContext).consumeAsFlow().collect {
                refreshScene()
            }
        }
        launchWithAppContext {
            EventBus.events.filterIsInstance<UIEvent.InspectAgent>().collectLatest { event ->
                objectTree.value.children.asSequence()
                    .filterIsInstance<AgentsFolder>()
                    .map { it.children }
                    .flatten()
                    .filterIsInstance<Folder>()
                    .map { it.children }
                    .flatten()
                    .filterIsInstance<Agent>()
                    .firstOrNull { it.id == event.id }
                    ?.let {
                        _selectedEntity.value = it
                    }
            }
        }
    }

    private fun refreshScene() {
        launchWithAppContext {
            when (val selectedEntity = getSelectedEntity()) {
                null -> {
                    _selectedEntity.value = None
                    _components.value = emptyList()
                    _availableComponents.value = emptyMap()
                }
                else -> {
                    _components.value = componentManager.getComponentDtoList(selectedEntity.getComponents())
                    _availableComponents.value = sceneService.getComponentTree(selectedEntity)
                }
            }
            _objectTree.value = Folder("root", listOf(Environment, Experimenter, AgentsFolder(buildAgentTree())))
        }
    }

    fun selectEntity(entity: Entity) {
        _selectedEntity.value = entity
        if (entity is Agent) {
            launchWithAppContext {
                EventBus.publish(UIEvent.InspectAgent(entity.id))
            }
        }
    }

    private fun buildAgentTree(): List<AgentPrototype> {
        val agentTypeAgentMap = sceneService.scene.agents.entries
            .groupBy { it.value.agentType }
            .toSortedMap()
            .map { (agentType, idAgentMap) ->
                val sortedIdAgentMap = idAgentMap.asSequence()
                    .map { it.key to it.value }
                    .toMap()
                    .toSortedMap()
                val type = agentType.splitOnCapitalChars()
                agentType to sortedIdAgentMap.map { Agent(type, it.key) }
            }.toMap()
        return sceneService.scene.agentPrototypes.keys.map {
            AgentPrototype(it, agentTypeAgentMap.getOrDefault(it, listOf()))
        }
    }

    private fun getSelectedEntity(): core.entities.Entity? {
        val scene = sceneService.scene
        return when (val entity = _selectedEntity.value) {
            is Experimenter -> scene.experimenter
            is Environment -> scene.environment
            is Agent -> scene.agents[entity.id]
            is AgentPrototype -> scene.agentPrototypes[entity.type]
            is None -> {
                _components.value = emptyList()
                null
            }
        }
    }

    fun changeComponentProperties(componentId: Int, propInspectorNode: ObjectNode) {
        launchWithAppContext {
            componentManager.changeComponentProperties(componentId, propInspectorNode)
        }
    }

    fun removeComponent(id: Int) {
        launchWithAppContext {
            val component = componentManager.getComponentById(id) ?: return@launchWithAppContext
            val entity = getSelectedEntity() ?: return@launchWithAppContext
            entity.removeComponent(component::class)
        }
    }

    fun addComponent(id: Int) {
        launchWithAppContext {
            val entity = getSelectedEntity() ?: return@launchWithAppContext
            sceneService.getComponentById(id)?.let {
                entity.setComponent(it)
            }
        }
    }

    fun changeRequestBody(name: String, propBuilderObjectNode: ObjectNode) {
        launchWithAppContext {
            componentManager.changeRequestBody(name, propBuilderObjectNode)
        }
    }

    fun commitRequestBody(name: String) {
        launchWithAppContext {
            componentManager.commitRequestBody(name)
        }
    }

    fun updateScriptsUI() {
        runBlocking {
            withContext(Contexts.app) { sceneService.updateScriptsUI() }
        }
    }

    private companion object {
        const val ROOT_FOLDER_TITLE = "root"
        const val REFRESH_DELAY_MS = 30L
    }
}

sealed class Node
sealed class FolderNode(open val title: String, open val children: List<Node>) : Node()
data class Folder(override val title: String, override val children: List<Node>) : FolderNode(title, children)
data class AgentsFolder(override val children: List<Node>) : FolderNode(getString("agent_folder_title"), children)

sealed class Entity : Node()
object None : Entity()
object Experimenter : Entity()
object Environment : Entity()
data class Agent(val type: String, val id: Int) : Entity()
data class AgentPrototype(val type: String, val agents: List<Agent>) : Entity()

sealed class ComponentDto(
    open val id: Int,
    open val name: String,
    open val removable: Boolean,
    open val properties: ObjectNode
)

data class Experiment(
    override val id: Int, override val name: String, override val properties: ObjectNode
) : ComponentDto(id, name, false, properties)

data class AgentInterface(
    override val id: Int,
    override val name: String,
    override val properties: ObjectNode,
    val requestBodies: List<Pair<String, ObjectNode>>
) : ComponentDto(id, name, false, properties)

data class Configuration(
    override val id: Int, override val name: String, override val properties: ObjectNode
) : ComponentDto(id, name, false, properties)

data class UnknownComponent(
    override val id:
    Int,
    override val name: String,
    override val removable: Boolean,
    override val properties: ObjectNode
) : ComponentDto(id, name, removable, properties)