package widgets.inspector.component


import imgui.ImGui
import model.FolderNode
import model.Node
import widgets.Widget

class AddComponentsWidget : Widget {
    private val navigator = ComponentTreeNavigator().apply {
        onSelectFolderNode = this@AddComponentsWidget::onSelectFolderNode
    }
    private var folderName: String = ""
    private var nodesInFolder: Map<Int, Node> = mapOf()
    private var showAddComponentsList = false

    var enabled: Boolean = true
    var onSelectComponent: (id: Int) -> Unit by navigator::onSelectComponentNode

    private fun onSelectFolderNode(folderName: String, nodesInFolder: Map<Int, Node>) {
        this.folderName = folderName
        this.nodesInFolder = nodesInFolder
    }

    fun load(componentTree: Map<Int, Node>, rootNode: Int) {
        val currentNodeId = navigator.currentNodeId
        navigator.load(componentTree, rootNode)
        if (currentNodeId in componentTree) {
            navigator.navigate(currentNodeId)
        }
    }

    override fun draw() {
        if (!enabled) return
        drawButton()
        if (showAddComponentsList) {
            drawFolder()
        }
    }

    private fun drawButton() {
        if (ImGui.button("Add component")) {
            showAddComponentsList = !showAddComponentsList
        }
    }

    private fun drawFolder() {
        ImGui.sameLine()
        ImGui.text(folderName)
        ImGui.sameLine()
        ImGui.spacing()
        val width = ImGui.getWindowWidth()
        ImGui.setNextItemWidth(width)
        if (ImGui.beginListBox("##$folderName")) {
            if (!navigator.isRoot() && ImGui.selectable("...")) {
                navigator.back()
            }
            for ((id, node) in nodesInFolder) {
                ImGui.pushID(id)
                if (ImGui.selectable(formatTitle(node))) {
                    navigator.navigate(id)
                }
                ImGui.popID()
            }
            ImGui.endListBox()
        }
    }

    private fun formatTitle(node: Node): String {
        return when (node) {
            is FolderNode -> "> ${node.name}"
            else -> node.name
        }
    }

    fun hide() {
        showAddComponentsList = false
    }
}