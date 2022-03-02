package views

import imgui.ImGuiListClipper
import imgui.ImVec4
import imgui.callback.ImListClipperCallback
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
    private var filteredLogs: Sequence<Pair<String, Level>> = getFilteredLogs()
    private val openFilter = ImBoolean()
    private val showDebug = ImBoolean(true)
    private val showError = ImBoolean(true)
    private val showInfo = ImBoolean(true)

    private fun getFilteredLogs(): Sequence<Pair<String, Level>> {
        return logs.asSequence().filter {
            val filterByLogLevels = it.second == Level.DEBUG && showDebug.get() ||
                    it.second == Level.INFO && showInfo.get() ||
                    it.second == Level.ERROR && showError.get()
            val filterBySubstring = filterString.get() in it.first
            filterByLogLevels && filterBySubstring
        }
    }

    fun log(text: String, level: Level) {
        val lines = text.split('\n').toMutableList()
        lines[0] = formatLog(lines[0], level)
        lines.forEach {
            if (logs.size < MAX_LOG_LIST_SIZE)
                logs.add(it to level)
        }
        if (logs.size == MAX_LOG_LIST_SIZE) {
            repeat(lines.size) {
                if (logs.isNotEmpty()) logs.removeFirst()
            }
        }
    }

    override fun draw() {
        if (ImGui.beginPopup("Options")) {
            ImGui.checkbox("Info", showInfo)
            ImGui.checkbox("Debug", showDebug)
            ImGui.checkbox("Error", showError)
            ImGui.checkbox("Filter", openFilter)
            ImGui.endPopup()
        }

        if (ImGui.smallButton("Options")) ImGui.openPopup("Options")

        ImGui.sameLine()
        if (ImGui.smallButton(TITLE_CLEAR_BUTTON)) {
            logs.clear()
        }
        ImGui.separator()

        if (openFilter.get()) {
            ImGui.setNextItemWidth(FILTER_WIDTH)
            ImGui.inputText("Filter", filterString)
            ImGui.separator()
        }

        val titleColor = ImGui.getStyle().getColor(ImGuiCol.FrameBg)
        ImGui.pushStyleColor(ImGuiCol.ChildBg, titleColor.x, titleColor.y, titleColor.z, titleColor.w)
        ImGui.beginChild(ID_LOG_CONTAINER)
        val filteredLogsList = filteredLogs.toList()
        ImGuiListClipper.forEach(filteredLogsList.size, object : ImListClipperCallback() {
            override fun accept(logIndex: Int) {
                val (text, level) = filteredLogsList[logIndex]
                val color = when (level) {
                    Level.ERROR -> ERROR_COLOR
                    Level.DEBUG -> ImGui.getStyle().getColor(ImGuiCol.Text)
                    Level.INFO -> ImGui.getStyle().getColor(ImGuiCol.Text)
                }
                ImGui.textColored(color.x, color.y, color.z, color.w, text)
            }
        })

        if (ImGui.getScrollY() == ImGui.getScrollMaxY()) {
            ImGui.setScrollHereY()
        }
        ImGui.endChild()
        ImGui.popStyleColor()
    }

    private fun formatLog(text: String, level: Level): String {
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

        val ERROR_COLOR = ImVec4(255f / 255f, 100f / 255f, 100f / 255f, 255f / 255f)
        val INFO_COLOR = listOf(255, 255, 255, 255)
        val DEBUG_COLOR = listOf(255, 255, 255, 255)

        const val ID_LOG_CONTAINER = "log container"
        const val FILTER_WIDTH = 300f
    }
}