package app.components.experiment.goals

import app.components.experiment.view.COLOR_EV_GOOD
import app.components.experiment.view.COLOR_IV_GOOD
import app.components.experiment.view.TableView
import core.datatypes.base.Series
import imgui.ImColor
import imgui.flag.ImGuiTableBgTarget
import imgui.internal.ImGui
import java.util.*

class GoalsView(dataSource: Map<String, Series<*>>, rowTypes: Series<Int>) :
    TableView(TITLE_WINDOW, dataSource, rowTypes) {
    var targetScore = 0.0
    var goalNameToRatingMap = mapOf<String, Double>()

    override fun formatTitle(title: String, columnIndex: Int): String {
        if (columnIndex in 1 until columnNumber - 1) {
            return GOAL_TITLE.format(Locale.US, title, goalNameToRatingMap[title])
        }
        else if(columnIndex == columnNumber - 1) {
            return TOTAL_SCORE_TITLE.format(Locale.US, title, targetScore)
        }
        return super.formatTitle(title, columnIndex)
    }

    override fun drawCell(column: Int, row: Int, value: Any?, rowType: Int) {
        val bgColor = when(rowType) {
            1 -> COLOR_EV_GOOD
            else -> COLOR_IV_GOOD
        }
        when (column) {
            columnNumber - 1 -> {
                val totalScore = value as Double
                super.drawCell(column, row, TOTAL_SCORE.format(Locale.US, totalScore), rowType)
                if (totalScore >= targetScore) {
                    ImGui.tableSetBgColor(ImGuiTableBgTarget.CellBg, bgColor)
                }
            }
            COLUMN_T -> {
                super.drawCell(column, row, value, rowType)
            }
            else -> {
                super.drawCell(column, row, CURRENT_VALUE.format(Locale.US, value), rowType)
                value as Double
                if (value >= goalNameToRatingMap[indexNameMap[column]]!!)
                    ImGui.tableSetBgColor(ImGuiTableBgTarget.CellBg, bgColor)
            }
        }
    }

    private companion object {
        const val TITLE_WINDOW = "Goals"
        const val TOTAL_SCORE = "%.3f"
        const val CURRENT_VALUE = "%.3f"
        const val GOAL_TITLE = "%s (%.3f)"
        const val TOTAL_SCORE_TITLE = "%s (%.3f)"
        const val COLUMN_T = 0
    }
}