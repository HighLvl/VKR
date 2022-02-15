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

    override fun draw() {
        drawDockspace()
        dockWindows()
    }

    private fun drawDockspace() {
        val viewport = ImGui.getMainViewport()
        ImGui.setNextWindowSize(viewport.sizeX, viewport.sizeY)
        ImGui.setNextWindowPos(viewport.posX, viewport.posY)
        ImGui.setNextWindowViewport(viewport.id)

        val flags = ImGuiWindowFlags.NoMove or
                ImGuiWindowFlags.NoBringToFrontOnFocus or
                ImGuiWindowFlags.NoResize or
                ImGuiWindowFlags.NoScrollbar or
                ImGuiWindowFlags.NoSavedSettings or
                ImGuiWindowFlags.NoTitleBar

        if (ImGui.begin("master_window_id", flags)) {
            id = ImGui.getID("central_dockspace_id")
            ImGui.dockSpace(id)
        }
        ImGui.end()
    }

    private fun dockWindows() {
        if (docked) return
        ImGui.dockBuilderRemoveNode(id)
        ImGui.dockBuilderAddNode(id, ImGuiDockNodeFlags.DockSpace)
        val positionIdMap = splitDockspace()
        for ((position, window) in dockedWindows.entries) {
            val dockId = positionIdMap[position]!!
            ImGui.dockBuilderSetNodeSize(dockId, window.width, window.height)
            ImGui.dockBuilderDockWindow(window.name, dockId)
        }
        ImGui.dockBuilderFinish(id)
        docked = true
    }

    private fun splitDockspace(): Map<Position, Int> {
        val up = ImInt()
        val down = ImGui.dockBuilderSplitNode(id, ImGuiDir.Down, .25f, null, up)
        val upRight = ImInt()
        val upLeft = ImGui.dockBuilderSplitNode(up.get(), ImGuiDir.Left, .25f, null, upRight)
        return mapOf(
            Position.DOWN to down,
            Position.UP_LEFT to upLeft,
            Position.UP_RIGHT to upRight.get()
        )
    }

    fun dock(window: Window, position: Position) {
        dockedWindows[position] = window
    }

    enum class Position {
        DOWN, UP_LEFT, UP_RIGHT
    }
}