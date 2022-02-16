package views.properties

import imgui.ImGui

abstract class MutableProperty<T>(
    name: String,
    initValue: T,
    protected val onValueChange: (value: T) -> Unit
) : Property(name) {
    open var value: T = initValue

    override fun draw() {
        ImGui.pushID(name)
        super.draw()
        ImGui.popID()
    }
}