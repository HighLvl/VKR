package viewmodel

import app.services.scene.SceneService
import com.fasterxml.jackson.databind.node.ObjectNode
import core.utils.splitOnCapitalChars
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.channels.ticker
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.runBlocking
import model.ComponentRepository

@OptIn(ObsoleteCoroutinesApi::class)
class SceneViewModel(
    private val sceneService: SceneService,
    private val componentRepository: ComponentRepository
) :
    ViewModel() {
    private val _components = MutableStateFlow<List<ComponentDto>>(listOf())
    private val _selectedEntity = MutableStateFlow<Entity>(None)
    private val _objectTree = MutableStateFlow(Folder(ROOT_FOLDER_TITLE, listOf()))
    private val _canAddComponents = MutableStateFlow(componentRepository.getComponentTree())
    private val componentManager = ComponentManager()

    val components = _components.asStateFlow()
    val selectedEntity = _selectedEntity.asStateFlow()
    val objectTree = _objectTree.asStateFlow()
    val canAddComponents = _canAddComponents.asStateFlow()

    init {
        launchWithAppContext {
            ticker(REFRESH_DELAY_MS, REFRESH_DELAY_MS, coroutineContext).consumeAsFlow().collect {
                refreshScene()
            }
        }
    }

    private fun refreshScene() {
        launchWithAppContext {
            when (val selectedEntity = getSelectedEntity()) {
                null -> {
                    _selectedEntity.value = None
                    _components.value = emptyList()
                }
                else -> {
                    _components.value = componentManager.getComponentDtoList(selectedEntity.getComponents())
                }
            }
            _objectTree.value = Folder("root", listOf(Environment, Experimenter, AgentsFolder(buildAgentList())))
        }
    }

    fun selectEntity(entity: Entity) {
        _selectedEntity.value = entity
    }

    private fun buildAgentList() = sceneService.scene.agents.entries
        .groupBy { it.value.agentType }
        .toSortedMap()
        .map { (agentType, idAgentMap) ->
            val sortedIdAgentMap = idAgentMap.asSequence()
                .map { it.key to it.value }
                .toMap()
                .toSortedMap()
            val type = agentType.splitOnCapitalChars()
            Folder(type, sortedIdAgentMap.map { Agent(it.key, type) })
        }

    private fun getSelectedEntity(): core.entities.Entity? {
        val scene = sceneService.scene
        return when (val entity = _selectedEntity.value) {
            is Experimenter -> scene.experimenter
            is Environment -> scene.environment
            is Agent -> scene.agents[entity.id]
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
            componentRepository.getComponentById(id)?.let {
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
            launchWithAppContext {
                sceneService.updateScriptsUI()
            }.join()
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
data class AgentsFolder(override val children: List<Node>) : FolderNode("Agents", children)
sealed class Entity : Node()
object None : Entity()
object Experimenter : Entity()
object Environment : Entity()
data class Agent(val id: Int, val type: String) : Entity()

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