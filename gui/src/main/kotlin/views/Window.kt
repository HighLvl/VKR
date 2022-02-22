package views

import imgui.internal.ImGui

class Window(
    val name: String,
    view: View,
    val width: Float = 0f,
    val height: Float = 0f,
) : Decorator(view) {

    private var isInit = false

    override fun draw() {
        if (!isInit) {
            if (width != 0f && height != 0f)
                ImGui.setNextWindowSize(width, height)
            isInit = true
        }

        if (ImGui.begin(name)) {
            super.draw()
        }
        ImGui.end()
    }
}