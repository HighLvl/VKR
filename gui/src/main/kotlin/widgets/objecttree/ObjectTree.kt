package widgets.objecttree

import imgui.ImGui
import imgui.flag.ImGuiTreeNodeFlags
import widgets.Widget

class ObjectTree : Widget {
    private val nodes = mutableListOf<TreeNode>()
    override fun draw() {
        nodes.forEach { it.draw() }
    }

    fun addNode(node: TreeNode) {
        nodes.add(node)
    }

    fun clear() {
        nodes.clear()
    }
}

sealed class TreeNode(protected val name: String) : Widget
class ObjectNode(name: String, private val onClickListener: () -> Unit = {}) : TreeNode(name) {
    override fun draw() {
        val flags = ImGuiTreeNodeFlags.Leaf or ImGuiTreeNodeFlags.Bullet or ImGuiTreeNodeFlags.NoTreePushOnOpen
        ImGui.treeNodeEx(name, flags, name)
        if (ImGui.isItemClicked()) {
            onClickListener()
        }
    }
}

class FolderNode(name: String, private val onClickListener: (() -> Unit)? = null) : TreeNode(name) {
    private val nodes = mutableListOf<TreeNode>()
    fun addNode(node: TreeNode) {
        nodes.add(node)
    }

    override fun draw() {
        if (onClickListener == null) {
            drawTreeNode()
        } else {
            drawClickableTreeNode(onClickListener)
        }
    }

    private fun drawTreeNode() {
        ImGui.pushID(name)
        if (ImGui.treeNode(name, name)) {
            nodes.forEach { it.draw() }
            ImGui.treePop()
        }
        ImGui.popID()
    }

    private fun drawClickableTreeNode(onClickListener: () -> Unit) {
        ImGui.pushID(name)
        val opened = ImGui.treeNode("")
        ImGui.sameLine()
        ImGui.selectable(name)
        if (ImGui.isItemClicked()) {
            onClickListener()
        }
        if (opened) {
            nodes.forEach { it.draw() }
            ImGui.treePop()
        }
        ImGui.popID()

    }

}