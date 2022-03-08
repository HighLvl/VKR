package dockspace

import imgui.internal.ImGui
import widgets.Decorator
import widgets.Widget

open class Window(
    val name: String,
    widget: Widget,
    val width: Float = 0f,
    open val height: Float = 0f,
) : Decorator(widget) {

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