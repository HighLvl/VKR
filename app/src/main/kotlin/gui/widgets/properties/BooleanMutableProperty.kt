package gui.widgets.properties

import imgui.ImGui
import imgui.type.ImBoolean

class BooleanMutableProperty(name: String, initValue: Boolean, onValueChange: (Boolean) -> Unit = {}) :
    MutableProperty<Boolean>(
        name,
        initValue,
        onValueChange
    ) {
    private val inputValue = ImBoolean(initValue)

    override var value: Boolean
        get() = inputValue.get()
        set(value) {
            inputValue.set(value)
        }

    override fun drawValue() {
        if (ImGui.checkbox(LABEL_TEXT, inputValue)
        ) {
            onChangeValue(value)
        }
    }

    companion object {
        const val LABEL_TEXT = ""
    }
}