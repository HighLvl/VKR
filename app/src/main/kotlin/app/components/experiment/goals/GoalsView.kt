package app.components.experiment.goals

import app.components.experiment.view.TableView
import core.datatypes.base.Series
import imgui.ImColor
import imgui.flag.ImGuiTableBgTarget
import imgui.internal.ImGui
import java.util.*

class GoalsView(dataSource: Map<String, Series<*>>) : TableView(TITLE_WINDOW, dataSource) {


    override fun drawCell(columnTitle: String, row: Int, value: Any?) {
        if (columnTitle == TITLE_TOTAL_SCORE) {
            val (totalScore, targetScore) = value as Pair<*, *>
            targetScore as Double; totalScore as Double
            super.drawCell(columnTitle, row, TOTAL_SCORE_TARGET_SCORE.format(Locale.US, totalScore, targetScore))
            if (totalScore >= targetScore) {
                ImGui.tableSetBgColor(ImGuiTableBgTarget.CellBg, ImColor.intToColor(100, 255, 100, 255))
            }
            return
        }
        super.drawCell(columnTitle, row, value)
        if (columnTitle !in nonGoalNames) {
            value as Double
            if (value != 0)
                ImGui.tableSetBgColor(ImGuiTableBgTarget.CellBg, ImColor.intToColor(100, 255, 100, 255))
        }
    }

    private companion object {
        const val TITLE_WINDOW = "Goals"
        const val TITLE_TOTAL_SCORE = "Total Score"
        const val TOTAL_SCORE_TARGET_SCORE = "%.1f/%.1f"
        val nonGoalNames = setOf("t", TITLE_TOTAL_SCORE)
    }
}