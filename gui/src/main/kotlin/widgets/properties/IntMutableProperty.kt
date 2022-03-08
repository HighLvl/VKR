package widgets.properties

import imgui.ImGui
import imgui.type.ImInt
import org.lwjgl.glfw.GLFW

class IntMutableProperty(
    name: String, initValue: Int, onValueChange: (value: Int) -> Unit = {}

) : MutableProperty<Int>(name, initValue, onValueChange) {

    private val inputValue = ImInt(initValue)

    override var value: Int
        get() = inputValue.get()
        set(value) {
            inputValue.set(value)
        }

    override fun drawValue() {
        if (ImGui.inputInt(LABEL_TEXT, inputValue)
        ) {
            val isActive = ImGui.isItemActive()
            if (isActive && ImGui.isKeyPressed(GLFW.GLFW_KEY_ENTER) || !isActive)
                onChangeValue(value)
        }
    }

    companion object {
        const val LABEL_TEXT = "int"
    }
}