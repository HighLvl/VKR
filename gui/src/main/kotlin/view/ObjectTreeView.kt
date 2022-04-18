package view

import utils.getString
import viewmodel.*
import widgets.Widget
import widgets.objecttree.FolderNode
import widgets.objecttree.ObjectNode
import widgets.objecttree.ObjectTree

class ObjectTreeView(private val viewModel: SceneViewModel) : View(), Widget {
    private val objectTree = ObjectTree()

    override fun onPreRun() {
        viewModel.objectTree.collectWithUiContext {
            objectTree.clear()
            it.children.forEach(this::handleNode)
        }
    }

    private fun handleNode(
        node: Node
    ) {
        when (node) {
            is Environment -> objectTree.addNode(
                ObjectNode(TITLE_ENVIRONMENT_OBJECT) {
                    viewModel.selectEntity(node)
                }
            )
            is Experimenter -> objectTree.addNode(
                ObjectNode(TITLE_EXPERIMENTER_OBJECT) {
                    viewModel.selectEntity(node)
                }
            )
            is AgentsFolder -> objectTree.addNode(buildAgentsFolderNode(node))
            else -> {
            }
        }
    }

    private fun buildAgentsFolderNode(node: AgentsFolder): FolderNode {
        return FolderNode(TITLE_AGENT_FOLDER).apply {
            node.children.forEach { agentPrototype ->
                agentPrototype as AgentPrototype
                addNode(buildAgentPrototypeFolderNode(agentPrototype))
            }
        }
    }

    private fun buildAgentPrototypeFolderNode(agentPrototype: AgentPrototype): FolderNode {
        return FolderNode(agentPrototype.type) {
            viewModel.selectEntity(agentPrototype)
        }.apply {
            agentPrototype.agents.forEach { agent ->
                addNode(ObjectNode(agent.id.toString()) {
                    viewModel.selectEntity(agent)
                })
            }
        }
    }

    override fun draw() {
        objectTree.draw()
    }

    private companion object {
        val TITLE_ENVIRONMENT_OBJECT = getString("environment_title")
        val TITLE_EXPERIMENTER_OBJECT = getString("experimenter_title")
        val TITLE_AGENT_FOLDER = getString("agent_folder_title")
    }
}