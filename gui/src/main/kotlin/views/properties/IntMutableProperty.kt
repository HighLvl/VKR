package views.properties

import imgui.ImGui
import imgui.type.ImInt
import org.lwjgl.glfw.GLFW

class IntMutableProperty(
    name: String,
    onValueChange: (value: Int) -> Unit
) : MutableProperty<Int>(name, onValueChange) {

    private val inputValue = ImInt()

    override var value: Int
        get() = inputValue.get()
        set(value) {
            inputValue.set(value)
        }

    override fun drawValue() {
        if (ImGui.inputInt(LABEL_TEXT, inputValue)
        ) {
            val focused = ImGui.isAnyItemFocused()
            if (focused && ImGui.isKeyPressed(GLFW.GLFW_KEY_ENTER) || !focused)
                onValueChange(inputValue.get())
        }
    }

    companion object {
        const val LABEL_TEXT = "int"
    }
}