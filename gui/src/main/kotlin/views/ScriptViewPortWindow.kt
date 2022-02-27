package views

import imgui.internal.ImGui

class ScriptViewPortWindow(name: String, private val view: View) : Window(name, view) {
    override fun drawWindow() {
        ImGui.begin(name)
        view.draw()
        ImGui.end()
    }
}