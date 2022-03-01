package controllers

import app.services.scene.SceneService
import app.utils.splitOnCapitalChars
import core.entities.Entity
import views.inspector.component.ComponentInspector
import views.objecttree.FolderNode
import views.objecttree.ObjectNode
import views.objecttree.ObjectTree

class SceneController(
    private val sceneService: SceneService,
    private val componentInspector: ComponentInspector,
    private val objectTree: ObjectTree,
    private val sceneSetup: SceneSetup
) : Controller() {
    private var selectedEntity: Pair<Entity, String>? = null

    override fun start() {
        super.start()
        sceneSetup.onLoadConfigurationListener = ::loadConfiguration
    }

    override fun update() {
        super.update()
        val scene = sceneService.scene
        val optimizer = scene.experimenter
        val environment = scene.environment
        val agents = scene.agents

        objectTree.apply {
            clear()
            addNode(ObjectNode(TITLE_ENVIRONMENT_OBJECT) {
                selectedEntity = environment to TITLE_ENVIRONMENT_OBJECT
            })
            addNode(ObjectNode(TITLE_OPTIMIZER_OBJECT) {
                selectedEntity = optimizer to TITLE_OPTIMIZER_OBJECT
            })
            addNode(FolderNode(TITLE_AGENT_FOLDER).apply {
                agents.entries
                    .groupBy { it.value.agentType }
                    .toSortedMap()
                    .forEach { (agentType, idAgentMap) ->
                        val sortedIdAgentMap = idAgentMap.asSequence()
                            .map { it.key to it.value }
                            .toMap()
                            .toSortedMap()
                        addNode(FolderNode(agentType.splitOnCapitalChars()).apply {
                            sortedIdAgentMap.forEach { (id, agent) ->
                                addNode(ObjectNode(id.toString()) {
                                    val name = TITLE_AGENT_OBJECT.format(agent.agentType, id)
                                    selectedEntity = agent to name
                                })
                            }
                        })
                    }
            })
        }

        updateComponentInspector(agents.values + optimizer + environment)
    }

    private fun updateComponentInspector(entities: List<Entity>) {
        val selectedEntity = selectedEntity
        selectedEntity?.let {
            if (selectedEntity.first !in entities) {
                this.selectedEntity = null
                return
            }
        }
        when (selectedEntity) {
            null -> {
                componentInspector.title = TITLE_OBJECT_NOT_SELECTED
                componentInspector.components = emptyList()
            }
            else -> {
                componentInspector.title = TITLE_COMPONENT_INSPECTOR.format(selectedEntity.second)
                componentInspector.components = selectedEntity.first.getComponents()
                componentInspector.onCloseComponent = { selectedEntity.first.removeComponent(it::class) }
            }
        }
    }

    private fun loadConfiguration(path: String) {
        sceneService.loadConfiguration(path)
    }

    override fun stop() {
        super.stop()
        sceneSetup.onLoadConfigurationListener = { }
        selectedEntity = null
    }

    private companion object {
        const val TITLE_ENVIRONMENT_OBJECT = "Environment"
        const val TITLE_OPTIMIZER_OBJECT = "Experimenter"
        const val TITLE_COMPONENT_INSPECTOR = "Inspected object: %s"
        const val TITLE_AGENT_OBJECT = "%s (%d)"
        const val TITLE_AGENT_FOLDER = "Agents"
        const val TITLE_OBJECT_NOT_SELECTED = "The object is not selected"
    }
}