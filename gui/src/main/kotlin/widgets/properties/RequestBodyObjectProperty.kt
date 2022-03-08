package widgets.properties

import imgui.ImGui

class RequestBodyObjectProperty(name: String, private val onMakeRequestClickListener: () -> Unit) :
    NodeTreeObjectProperty(name) {
    override fun drawValue() {
        super.drawValue()
        ImGui.nextColumn()
        repeat(3) { ImGui.spacing() }
        if (ImGui.button(MAKE_REQUEST_BUTTON_TITLE)) {
            onMakeRequestClickListener()
        }
        repeat(3) { ImGui.spacing() }
        ImGui.separator()
        ImGui.nextColumn()
    }

    companion object {
        const val MAKE_REQUEST_BUTTON_TITLE = "Schedule request"
    }
}