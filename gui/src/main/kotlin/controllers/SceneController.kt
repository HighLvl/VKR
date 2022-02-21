package controllers

import core.entities.Entity
import core.scene.Scene
import views.inspector.component.ComponentInspector
import views.objecttree.FolderNode
import views.objecttree.ObjectNode
import views.objecttree.ObjectTree

class SceneController(
    private val scene: Scene,
    private val componentInspector: ComponentInspector,
    private val objectTree: ObjectTree
) {
    private var selectedEntity: Pair<Entity, String>? = null

    fun update() {
        val optimizer = scene.optimizer
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
            }
            else -> {
                componentInspector.title = TITLE_COMPONENT_INSPECTOR.format(selectedEntity.second)
                componentInspector.components = selectedEntity.first.getComponents()
            }
        }
    }

    private companion object {
        const val TITLE_ENVIRONMENT_OBJECT = "Environment"
        const val TITLE_OPTIMIZER_OBJECT = "Optimizer"
        const val TITLE_COMPONENT_INSPECTOR = "Inspected object: %s"
        const val TITLE_AGENT_OBJECT = "%s (%d)"
        const val TITLE_AGENT_FOLDER = "Agents"
        const val TITLE_OBJECT_NOT_SELECTED = "The object is not selected"
    }
}