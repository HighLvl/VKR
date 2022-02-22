package views

import imgui.ImVec4
import imgui.flag.ImGuiCol
import imgui.flag.ImGuiStyleVar
import imgui.internal.ImGui
import imgui.internal.flag.ImGuiItemFlags

class Button(private val title: String) : View {
    var enabled = true
    var pressed = false

    var onClickListener = {}

    override fun draw() {
        if (enabled && pressed) {
            val color = ImVec4()
            ImGui.getStyleColorVec4(ImGuiCol.ButtonActive, color)
            ImGui.pushStyleColor(ImGuiCol.Button,  color.x, color.y, color.z, color.w)
        }

        if (!enabled) {
            ImGui.pushItemFlag(ImGuiItemFlags.Disabled, true)
            ImGui.pushStyleVar(ImGuiStyleVar.Alpha, ImGui.getStyle().alpha * 0.5f)
        }
        if (ImGui.button(title)) {
            onClickListener()
        }
        if (!enabled) {
            ImGui.popItemFlag()
            imgui.ImGui.popStyleVar()
        }
        if (enabled && pressed) {
            ImGui.popStyleColor()
        }
    }
}