package views.properties

import imgui.ImGui

class StringImmutableProperty(name: String) : ImmutableProperty<String>(name) {
    override var value: String = ""

    override fun drawValue() {
        ImGui.text(value)
    }
}