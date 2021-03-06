package gui.widgets.inspector.component

import app.services.repository.component.FolderNode
import app.services.repository.component.Node

class ComponentTreeNavigator {
    private val tree = mutableMapOf<Int, Node>()
    private var backStack = mutableListOf<Int>()

    var onSelectComponentNode: (nodeId: Int) -> Unit = {}
    var onSelectFolderNode: (folderName: String, nodesInFolder: Map<Int, Node>) -> Unit = { _, _ -> }
    val currentNodeId: Int
    get() = if(backStack.isEmpty()) 0 else backStack.last()

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
        if (currentNodeId == nodeId && backStack.isNotEmpty()) return
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