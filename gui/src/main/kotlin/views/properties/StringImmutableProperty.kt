package views.properties

import imgui.ImGui

class StringImmutableProperty(name: String) : Property(name) {
    private var string = ""

    fun setString(string: String) {
        this.string = string
    }

    override fun drawValue() {
        ImGui.text(string)
    }
}