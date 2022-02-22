package views

import imgui.ImVec4
import imgui.flag.ImGuiCol
import imgui.internal.ImGui
import imgui.type.ImBoolean
import imgui.type.ImString
import java.util.*

class LoggerView : View {
    enum class Level {
        ERROR, DEBUG, INFO
    }

    private val logs = LinkedList<Pair<String, Level>>()
    private val filterString = ImString()
    private var filteredLogs = logs.asSequence()
    private val openFilter = ImBoolean()

    fun log(text: String, level: Level) {
        if (logs.size == MAX_LOG_LIST_SIZE) {
            logs.removeFirst()
        }
        logs.add(text to level)
    }

    override fun draw() {
        if (ImGui.beginPopup("Options"))
        {
            ImGui.checkbox("Filter", openFilter);
            ImGui.endPopup();
        }

        if (ImGui.smallButton("Options")) ImGui.openPopup("Options")

        ImGui.sameLine()
        if (ImGui.smallButton(TITLE_CLEAR_BUTTON)) {
            logs.clear()
        }
        ImGui.separator()
        if (openFilter.get()) {
            ImGui.setNextItemWidth(FILTER_WIDTH)
            if (ImGui.inputText("Filter", filterString)) {
                filteredLogs = logs.asSequence().filter { filterString.get() in it.first }
            }
            ImGui.separator()
        }

        val titleColor = ImGui.getStyle().getColor(ImGuiCol.FrameBg)
        ImGui.pushStyleColor(ImGuiCol.ChildBg, titleColor.x, titleColor.y, titleColor.z, titleColor.w)
        ImGui.beginChild(ID_LOG_CONTAINER)
        for (log in filteredLogs) {
            val formattedLog = formatLog(log)
            val color = when (log.second) {
                Level.ERROR -> ERROR_COLOR
                Level.DEBUG -> ImGui.getStyle().getColor(ImGuiCol.Text)
                Level.INFO -> ImGui.getStyle().getColor(ImGuiCol.Text)
            }
            ImGui.textColored(color.x, color.y, color.z, color.w, formattedLog)
        }
        if (ImGui.getScrollY() == ImGui.getScrollMaxY()) {
            ImGui.setScrollHereY()
        }
        ImGui.endChild()
        ImGui.popStyleColor()
    }

    private fun formatLog(log: Pair<String, Level>): String {
        val (text, level) = log
        return when (level) {
            Level.ERROR -> FORMATTED_ERROR_LOG.format(text)
            Level.INFO -> FORMATTED_INFO_LOG.format(text)
            Level.DEBUG -> FORMATTED_DEBUG_LOG.format(text)
        }
    }

    companion object {
        const val MAX_LOG_LIST_SIZE = 10000

        const val FORMATTED_ERROR_LOG = "[error] %s"
        const val FORMATTED_INFO_LOG = "[info] %s"
        const val FORMATTED_DEBUG_LOG = "[debug] %s"
        const val TITLE_CLEAR_BUTTON = "Clear"


        val ERROR_COLOR = ImVec4(255f /255f, 100f/255f, 100f/255f, 255f/255f)
        val INFO_COLOR = listOf(255, 255, 255, 255)
        val DEBUG_COLOR = listOf(255, 255, 255, 255)

        const val ID_LOG_CONTAINER = "log container"
        const val FILTER_WIDTH = 300f
    }
}