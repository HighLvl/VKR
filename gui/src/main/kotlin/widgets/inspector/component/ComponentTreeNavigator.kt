package widgets.inspector.component

import model.FolderNode
import model.Node

class ComponentTreeNavigator {
    private val tree = mutableMapOf<Int, Node>()
    private var backStack = mutableListOf<Int>()

    var onSelectComponentNode: (nodeId: Int) -> Unit = {}
    var onSelectFolderNode: (folderName: String, nodesInFolder: Map<Int, Node>) -> Unit = { _, _ -> }

    fun load(componentTree: Map<Int, Node>, rootNodeId: Int) {
        tree.clear()
        tree.putAll(componentTree)
        backStack.clear()
        backStack.add(rootNodeId)
        navigateToRoot()
    }

    fun navigateToRoot() {
        val rootNode = backStack.first()
        backStack.clear()
        navigate(rootNode)
    }

    fun navigate(nodeId: Int) {
        val node = tree[nodeId] ?: return
        when (node) {
            is FolderNode -> {
                backStack.add(nodeId)
                onSelectFolderNode(node.name, node.nodes.associateWith { id -> tree[id]!! })
            }
            else -> {
                onSelectComponentNode(nodeId)
            }
        }
    }

    fun isRoot() = backStack.size <= 1

    fun back() {
        if (backStack.size <= 1) return
        backStack.removeLast()
        navigate(backStack.removeLast())
    }
}