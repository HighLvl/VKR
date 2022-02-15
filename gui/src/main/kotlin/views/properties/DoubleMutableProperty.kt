package views.properties

import imgui.ImGui
import imgui.type.ImDouble
import org.lwjgl.glfw.GLFW

class DoubleMutableProperty(name: String, onValueChange: (Double) -> Unit) : MutableProperty<Double>(
    name,
    onValueChange
) {
    private val inputValue = ImDouble()

    override var value: Double
        get() = inputValue.get()
        set(value) {
            inputValue.set(value)
        }

    override fun drawValue() {
        if (ImGui.inputDouble(LABEL_TEXT, inputValue)
        ) {
            //TODO not working for any
            val focused = ImGui.isAnyItemFocused()
            if (focused && ImGui.isKeyPressed(GLFW.GLFW_KEY_ENTER) || !focused)
                onValueChange(inputValue.get())
        }
    }

    companion object {
        const val LABEL_TEXT = "double"
    }
}