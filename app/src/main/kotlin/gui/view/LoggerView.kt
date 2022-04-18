package gui.view

import imgui.ImGuiListClipper
import imgui.ImVec4
import imgui.callback.ImListClipperCallback
import imgui.flag.ImGuiCol
import imgui.internal.ImGui
import imgui.type.ImBoolean
import imgui.type.ImString
import gui.utils.getString
import gui.viewmodel.Level
import gui.viewmodel.LoggerViewModel
import gui.widgets.Widget

class LoggerView(private val viewModel: LoggerViewModel) : View(), Widget {
    private val filterString = ImString()
    private val openFilter = ImBoolean()
    private val showDebug = ImBoolean(false)
    private val showError = ImBoolean(true)
    private val showInfo = ImBoolean(true)
    private var logs = listOf<Pair<String, Level>>()

    override fun onPreRun() {
        viewModel.logs.collectWithUiContext() {
            logs = it
        }
        filter()
    }

    private fun filter() {
        val levelSet = mutableSetOf<Level>()
        if (showInfo.get()) levelSet.add(Level.INFO)
        if (showError.get()) levelSet.add(Level.ERROR)
        if (showDebug.get()) levelSet.add(Level.DEBUG)
        viewModel.filter(levelSet, filterString.get())
    }

    override fun draw() {
        if (ImGui.beginPopup(getString("logger_options"))) {
            if (ImGui.checkbox(getString("logger_info_option"), showInfo)) {
                filter()
            }
            if (ImGui.checkbox(getString("logger_debug_option"), showDebug)) {
                filter()
            }
            if (ImGui.checkbox(getString("logger_error_option"), showError)) {
                filter()
            }
            if (ImGui.checkbox(getString("logger_filter_option"), openFilter)) {
                filter()
            }
            ImGui.endPopup()
        }

        if (ImGui.smallButton(getString("logger_options"))) ImGui.openPopup(getString("logger_options"))

        ImGui.sameLine()
        if (ImGui.smallButton(TITLE_CLEAR_BUTTON)) {
            viewModel.clear()
        }
        ImGui.separator()

        if (openFilter.get()) {
            ImGui.setNextItemWidth(FILTER_WIDTH)
            if (ImGui.inputText(getString("logger_filter_option"), filterString)) {
                filter()
            }
            ImGui.separator()
        }

        val titleColor = ImGui.getStyle().getColor(ImGuiCol.FrameBg)
        ImGui.pushStyleColor(ImGuiCol.ChildBg, titleColor.x, titleColor.y, titleColor.z, titleColor.w)
        ImGui.beginChild(ID_LOG_CONTAINER)
        ImGuiListClipper.forEach(logs.size, object : ImListClipperCallback() {
            override fun accept(logIndex: Int) {
                val (text, level) = logs[logIndex]
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

    private companion object {
        val TITLE_CLEAR_BUTTON = getString("logger_clear_button_title")

        val ERROR_COLOR = ImVec4(255f / 255f, 100f / 255f, 100f / 255f, 255f / 255f)
        val INFO_COLOR = listOf(255, 255, 255, 255)
        val DEBUG_COLOR = listOf(255, 255, 255, 255)

        const val ID_LOG_CONTAINER = "log container"
        const val FILTER_WIDTH = 300f
    }
}