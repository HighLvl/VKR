package views.input

import imgui.internal.ImGui
import imgui.type.ImInt

class InputInt(initValue: Int, label: String, onChangeValue: (Int) -> Unit = {}) :
    InputValue<Int>(initValue, label, onChangeValue) {
    private val imValue = ImInt(super.value)
    override var value: Int
        get() = imValue.get()
        set(value) {
            imValue.set(value)
        }

    override fun drawInput() {
        if (ImGui.inputInt(label, imValue)) {
            onChangeValueListener(imValue.get())
        }
    }
}