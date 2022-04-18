package gui.widgets.input

import imgui.internal.ImGui
import imgui.type.ImInt

class InputInt(initValue: Int, label: String, onChangeValue: (Int) -> Unit = {}) :
    InputValue<Int>(initValue, label, onChangeValue) {
    private val imValue = ImInt(super.value)

    override fun drawInput() {
        imValue.set(value)
        if (ImGui.inputInt(label, imValue)) {
            onChangeValueListener(imValue.get())
        }
    }
}