package views

import imgui.internal.ImGui
import java.util.*

class LoggerView : View {
    enum class Level {
        ERROR, DEBUG, INFO
    }

    private val logs = LinkedList<Pair<String, Level>>()

    fun log(text: String, level: Level) {
        if (logs.size == MAX_LOG_LIST_SIZE) {
            logs.removeFirst()
        }
        logs.add(text to level)
    }

    override fun draw() {
        if (ImGui.smallButton("Debug")) {
            log("Some debug", Level.DEBUG)
        }
        ImGui.sameLine()
        if (ImGui.smallButton("Error")) {
            log("Some error", Level.ERROR)
        }
        ImGui.sameLine()
        if (ImGui.smallButton("Info")) {
            log("Some info", Level.INFO)
        }
        ImGui.sameLine()
        if (ImGui.smallButton(TITLE_CLEAR_BUTTON)) {
            logs.clear()
        }
        ImGui.separator()

        ImGui.beginChild(ID_LOG_CONTAINER)
        for (log in logs) {
            val formattedLog = formatLog(log)
            val color = when (log.second) {
                Level.ERROR -> ERROR_COLOR
                Level.DEBUG -> DEBUG_COLOR
                Level.INFO -> INFO_COLOR
            }
            ImGui.textColored(color[0], color[1], color[2], color[3], formattedLog)
        }
        if (ImGui.getScrollY() == ImGui.getScrollMaxY()) {
            ImGui.setScrollHereY()
        }
        ImGui.endChild()
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


        val ERROR_COLOR = listOf(255, 100, 100, 255)
        val INFO_COLOR = listOf(100, 100, 255, 255)
        val DEBUG_COLOR = listOf(10, 150, 10, 255)

        const val ID_LOG_CONTAINER = "log container"
    }
}