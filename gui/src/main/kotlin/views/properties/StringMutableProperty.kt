package views.properties

import imgui.ImGui
import imgui.type.ImString
import org.lwjgl.glfw.GLFW

class StringMutableProperty(name: String, initValue: String, onValueChange: (String) -> Unit) : MutableProperty<String>(
    name,
    initValue, onValueChange
) {
    private val inputValue = ImString(initValue, 100)

    override var value: String
        get() = inputValue.get()
        set(value) {
            inputValue.set(value)
        }

    override fun drawValue() {
        ImGui.captureKeyboardFromApp()
        if (ImGui.inputText(LABEL_TEXT, inputValue)
        ) {
            val focused = ImGui.isAnyItemFocused()
            if (focused && ImGui.isKeyPressed(GLFW.GLFW_KEY_ENTER) || !focused)
                onValueChange(value)
        }
    }

    companion object {
        const val LABEL_TEXT = "str"
    }
}