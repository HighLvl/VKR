package gui.widgets.input

import imgui.flag.ImGuiStyleVar
import imgui.internal.ImGui
import imgui.internal.flag.ImGuiItemFlags
import gui.widgets.Widget

abstract class InputValue<T : Any>(
    initValue: T,
    protected val label: String,
    var onChangeValueListener: (value: T) -> Unit
) : Widget {
    open var value: T = initValue
    var enabled: Boolean = true

    override fun draw() {
        if (!enabled) {
            ImGui.pushItemFlag(ImGuiItemFlags.Disabled, true)
            ImGui.pushStyleVar(ImGuiStyleVar.Alpha, ImGui.getStyle().alpha * 0.5f)
        }
        drawInput()
        if (!enabled) {
            ImGui.popItemFlag()
            imgui.ImGui.popStyleVar()
        }
    }

    protected abstract fun drawInput()
}