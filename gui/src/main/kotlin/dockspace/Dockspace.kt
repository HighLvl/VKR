package dockspace

import imgui.flag.ImGuiDir
import imgui.flag.ImGuiWindowFlags
import imgui.internal.ImGui
import imgui.internal.flag.ImGuiDockNodeFlags
import imgui.type.ImInt
import widgets.Widget

class Dockspace : Widget {

    private var id = 0
    private var docked = false
    private val dockedWindows = mutableMapOf<Position, Window>()
    private var toolsWindow: ToolsWindow? = null

    var width: Float = 0f
    var height: Float = 0f
    var posY: Float = 0f
    var posX: Float = 0f
    var viewportId: Int = 0

    override fun onPreRun() {
        toolsWindow?.onPreRun()
        dockedWindows.values.forEach { it.onPreRun() }
    }

    override fun draw() {
        drawDockspace()
        dockWindows()
        drawWindows()
    }

    private fun drawWindows() {
        toolsWindow?.draw()
        dockedWindows.values.forEach { it.draw() }
    }

    private fun drawDockspace() {
        val toolsWindowHeight = toolsWindow?.height ?: 0f
        val viewport = ImGui.getMainViewport()
        height = viewport.sizeY - toolsWindowHeight
        width = viewport.sizeX
        viewportId = viewport.id
        posX = viewport.posX
        posY = viewport.posY + toolsWindowHeight
        ImGui.setNextWindowPos(posX, posY)
        ImGui.setNextWindowViewport(viewportId)
        ImGui.setNextWindowSize(width, height)
        val flags = ImGuiWindowFlags.NoMove or
                ImGuiWindowFlags.NoBringToFrontOnFocus or
                ImGuiWindowFlags.NoResize or
                ImGuiWindowFlags.NoScrollbar or
                ImGuiWindowFlags.NoSavedSettings or
                ImGuiWindowFlags.NoTitleBar
        if (ImGui.begin(ID_MASTER_WINDOW, flags)) {
            id = ImGui.getID(ID_CENTRAL_DOCKSPACE)
            ImGui.dockSpace(id)
        }
        ImGui.end()
    }



    private fun dockWindows() {
        if (docked) return
        ImGui.dockBuilderRemoveNode(id)
        ImGui.dockBuilderAddNode(id, ImGuiDockNodeFlags.DockSpace)
        ImGui.dockBuilderSetNodeSize(id, width, height)
        val positionIdMap = splitDockspace()
        for ((position, window) in dockedWindows.entries) {
            val dockId = positionIdMap[position]!!
            ImGui.dockBuilderDockWindow(window.name, dockId)
        }
        ImGui.dockBuilderFinish(id)
        docked = true
    }

    private fun splitDockspace(): Map<Position, Int> {
        val left = ImInt()
        val right = ImGui.dockBuilderSplitNode(id, ImGuiDir.Right, .25f, null, left)
        val leftUp = ImInt()
        val leftDown = ImGui.dockBuilderSplitNode(left.get(), ImGuiDir.Down, .25f, null, leftUp)
        val leftUpLeft = ImInt()
        val leftUpRight = ImGui.dockBuilderSplitNode(leftUp.get(), ImGuiDir.Right, 0.75f, null, leftUpLeft)
        return mapOf(
            Position.RIGHT to right,
            Position.LEFT_DOWN to leftDown,
            Position.LEFT_UP_RIGHT to leftUpRight,
            Position.LEFT_UP_LEFT to leftUpLeft.get()
        )
    }

    fun dock(window: Window, position: Position) {
        dockedWindows[position] = window
    }

    fun dockToolsWindow(window: ToolsWindow) {
        toolsWindow = window
    }

    enum class Position {
        RIGHT, LEFT_DOWN, LEFT_UP_RIGHT, LEFT_UP_LEFT
    }

    private companion object {
        const val ID_MASTER_WINDOW = "master_window_id"
        const val ID_CENTRAL_DOCKSPACE = "central_dockspace_id"
    }
}