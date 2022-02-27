package views

import imgui.internal.ImGui

open class Window(
    val name: String,
    view: View,
    val width: Float = 0f,
    val height: Float = 0f,
) : Decorator(view) {

    private var isInit = false

    protected open fun drawWindow() {
        if (ImGui.begin(name)) {
            super.draw()
        }
        ImGui.end()
    }

    override fun draw() {
        if (!isInit) {
            if (width != 0f && height != 0f)
                ImGui.setNextWindowSize(width, height)
            isInit = true
        }
        drawWindow()
    }
}