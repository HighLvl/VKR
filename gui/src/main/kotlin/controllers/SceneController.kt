package controllers

import app.services.scene.SceneService
import core.entities.Entity
import core.scene.Scene
import views.inspector.component.ComponentInspector
import views.objecttree.FolderNode
import views.objecttree.ObjectNode
import views.objecttree.ObjectTree

class SceneController(
    private val sceneService: SceneService,
    private val componentInspector: ComponentInspector,
    private val objectTree: ObjectTree,
    sceneSetup: SceneSetup
) {
    private var selectedEntity: Pair<Entity, String>? = null

    init {
        sceneSetup.onClearSceneListener = ::clearScene
        sceneSetup.onLoadConfigurationListener = ::loadConfiguration
    }

    fun update() {
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
                agents.forEach { (id, agent) ->
                    val name = TITLE_AGENT_OBJECT.format(agent.agentType, id)
                    addNode(ObjectNode(name) {
                        selectedEntity = agent to name
                    })
                }
            })
        }
        when (val selectedEntity = selectedEntity) {
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

    private fun clearScene() {
        selectedEntity = null
        sceneService.clearScene()
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