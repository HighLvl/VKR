package views

import imgui.ImVec4
import imgui.flag.ImGuiCol
import imgui.flag.ImGuiStyleVar
import imgui.internal.ImGui
import imgui.internal.flag.ImGuiItemFlags
import org.lwjgl.glfw.GLFW

class Button(private val title: String) : View {
    var enabled = true
    var pressed = false

    var onClickListener = {}
    private var boundKey: Int? = null
    private var formattedTitle = title

    fun bindKey(key: Key) {
        boundKey = key.mapToGLFWKey()
        formattedTitle = when (boundKey) {
            null -> title
            else -> "$title (${key.name})"
        }
    }

    override fun draw() {
        val enabledAndPressed = enabled && pressed
        val notEnabled = !enabled


        if (enabledAndPressed) {
            val color = ImVec4()
            ImGui.getStyleColorVec4(ImGuiCol.ButtonActive, color)
            ImGui.pushStyleColor(ImGuiCol.Button, color.x, color.y, color.z, color.w)
        }

        if (notEnabled) {
            ImGui.pushItemFlag(ImGuiItemFlags.Disabled, true)
            ImGui.pushStyleVar(ImGuiStyleVar.Alpha, ImGui.getStyle().alpha * 0.5f)
        }
        if (ImGui.button(formattedTitle) || isBoundKeyPressed()) {
            onClickListener()
        }
        if (notEnabled) {
            imgui.ImGui.popStyleVar()
            ImGui.popItemFlag()
        }
        if (enabledAndPressed) {
            ImGui.popStyleColor()
        }
    }

    private fun isBoundKeyPressed(): Boolean {
        val boundKey = boundKey ?: return false
        return ImGui.isKeyPressed(boundKey)
    }
}

private val mapping = mapOf(Key.R to GLFW.GLFW_KEY_R, Key.P to GLFW.GLFW_KEY_P)
private fun Key.mapToGLFWKey() = mapping[this]

enum class Key {
    R, P
}