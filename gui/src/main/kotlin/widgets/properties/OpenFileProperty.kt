package widgets.properties

import imgui.ImGui
import imgui.flag.ImGuiInputTextFlags
import imgui.type.ImString
import utils.getString
import widgets.FileOpenDialog

class OpenFileProperty(
    name: String,
    initValue: String,
    private val openFileButtonTitle: String = OPEN_FILE_BUTTON_TITLE,
    private val extensionFilter: String = ""
) : MutableProperty<String>(name, initValue) {
    private val filePath = ImString(initValue)
    override fun drawValue() {
        if (ImGui.button(openFileButtonTitle)) {
            val path = FileOpenDialog().open(extensionFilter)
            if (path.isNotBlank()) {
                onChangeValue(path)
            }
        }
        ImGui.sameLine()
        ImGui.inputText("", filePath, ImGuiInputTextFlags.ReadOnly)
    }

    private companion object {
        val OPEN_FILE_BUTTON_TITLE = getString("open_file_button_title")
    }
}