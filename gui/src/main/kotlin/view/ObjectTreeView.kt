package view

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
            node.children.forEach { agentTypeFolder ->
                agentTypeFolder as Folder
                addNode(buildAgentsTypeFolderNode(agentTypeFolder))
            }
        }
    }

    private fun buildAgentsTypeFolderNode(agentTypeFolder: Folder): FolderNode {
        return FolderNode(agentTypeFolder.title).apply {
            agentTypeFolder.children.forEach { agent ->
                agent as Agent
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
        const val TITLE_ENVIRONMENT_OBJECT = "Environment"
        const val TITLE_EXPERIMENTER_OBJECT = "Experimenter"
        const val TITLE_AGENT_FOLDER = "Agents"
    }
}