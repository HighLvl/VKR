package gui.dockspace

import imgui.internal.ImGui
import gui.view.ScriptViewPortView

class ScriptViewPortWindow(name: String, private val widget: ScriptViewPortView) : Window(name, widget) {
    override fun drawWindow() {
        ImGui.begin(name)
        widget.draw()
        ImGui.end()
    }
}