package dockspace

import imgui.flag.ImGuiWindowFlags
import imgui.internal.ImGui
import view.MenuBarView
import view.ModelControlView

class ToolsWindow(private val menuBarView: MenuBarView, private val modelControlView: ModelControlView) :
    Window("Tools window", modelControlView) {
    override val height: Float
    get() = menuBarView.height + MODEL_CONTROL_HEIGHT

    override fun onPreRun() {
        menuBarView.onPreRun()
        modelControlView.onPreRun()
    }

    override fun drawWindow() {
        val flags = ImGuiWindowFlags.NoMove or
                ImGuiWindowFlags.NoBringToFrontOnFocus or
                ImGuiWindowFlags.NoResize or
                ImGuiWindowFlags.NoScrollbar or
                ImGuiWindowFlags.NoSavedSettings or
                ImGuiWindowFlags.NoTitleBar
        ImGui.setNextWindowSize(menuBarView.width, MODEL_CONTROL_HEIGHT)
        ImGui.setNextWindowPos(menuBarView.posX, menuBarView.posY + menuBarView.height)
        if (ImGui.begin(name, flags)) {
            val width = modelControlView.width
            ImGui.setWindowPos(
                menuBarView.posX + (menuBarView.width - width) / 2,
                menuBarView.posY + menuBarView.height
            )
            menuBarView.draw()
            modelControlView.draw()
        }
        ImGui.end()
    }

    private companion object {
        const val MODEL_CONTROL_HEIGHT = 50f
    }
}