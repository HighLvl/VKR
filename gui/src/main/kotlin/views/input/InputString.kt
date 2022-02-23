package views.input

import imgui.internal.ImGui
import imgui.type.ImString

class InputString(initValue: String, label: String, charNumber: Int, onChangeValue: (String) -> Unit = {}) :
    InputValue<String>(initValue, label, onChangeValue) {
    private val imValue = ImString(super.value, charNumber)
    override var value: String
        get() = imValue.get()
        set(value) {
            imValue.set(value)
        }

    override fun drawInput() {
        if (ImGui.inputText(label, imValue)) {
            onChangeValueListener(imValue.get())
        }
    }
}