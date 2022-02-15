package views

import imgui.internal.ImGui
import views.Decorator
import views.View

class Window(
    val name: String,
    val width: Float,
    val height: Float,
    view: View
): Decorator(view) {

    private var isInit = false

    override fun draw() {
        if (!isInit) {
            ImGui.setNextWindowSize(width, height)
            isInit = true
        }

        if (ImGui.begin(name)) {
            super.draw()
        }
        ImGui.end()
    }
}