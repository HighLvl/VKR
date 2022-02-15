package views.properties

import imgui.ImGui
import imgui.type.ImString
import org.lwjgl.glfw.GLFW

class StringMutableProperty(name: String, onValueChange: (String) -> Unit) : MutableProperty<String>(name,
    onValueChange
) {
    private val inputValue = ImString("")

    override var value: String
        get() = inputValue.get()
        set(value) {inputValue.set(value)}

    override fun drawValue() {
        ImGui.captureKeyboardFromApp()
        if (ImGui.inputText(LABEL_TEXT, inputValue)
        ) {
            val focused = ImGui.isAnyItemFocused()
            if (focused && ImGui.isKeyPressed(GLFW.GLFW_KEY_ENTER) || !focused)
                onValueChange(inputValue.get())
        }
    }

    companion object {
        const val LABEL_TEXT = "str"
    }
}