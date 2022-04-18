package gui.widgets.properties

import imgui.ImGui

abstract class MutableProperty<T : Any>(
    name: String,
    initValue: T,
    var onChangeValue: (value: T) -> Unit = {}
) : Property(name) {
    open var value: T = initValue

    override fun draw() {
        ImGui.pushID(name)
        super.draw()
        ImGui.popID()
    }
}