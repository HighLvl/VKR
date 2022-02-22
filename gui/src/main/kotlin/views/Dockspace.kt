package views

import imgui.flag.ImGuiDir
import imgui.flag.ImGuiWindowFlags
import imgui.internal.ImGui
import imgui.internal.flag.ImGuiDockNodeFlags
import imgui.type.ImInt

class Dockspace : View {
    private var id = 0
    private var docked = false
    private val dockedWindows = mutableMapOf<Position, Window>()
    var width: Float = 0f
    var height: Float = 0f

    override fun draw() {
        drawDockspace()
        dockWindows()
    }

    private fun drawDockspace() {
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

    enum class Position {
        RIGHT, LEFT_DOWN, LEFT_UP_RIGHT, LEFT_UP_LEFT
    }

    private companion object {
        const val ID_MASTER_WINDOW = "master_window_id"
        const val ID_CENTRAL_DOCKSPACE = "central_dockspace_id"
    }
}