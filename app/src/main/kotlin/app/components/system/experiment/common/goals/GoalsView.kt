package app.components.system.experiment.common.goals

import app.components.system.experiment.common.view.COLOR_FV_GOOD
import app.components.system.experiment.common.view.COLOR_IV_GOOD
import app.components.system.experiment.common.view.TableView
import core.datatypes.base.Series
import imgui.flag.ImGuiTableBgTarget
import imgui.internal.ImGui
import java.util.*

class GoalsView(dataSource: Map<String, Series<*>>, rowTypes: Series<Int>) :
    TableView(TITLE_WINDOW, dataSource, rowTypes) {
    var targetScore = 0
    var goalNameToScoreMap = mapOf<String, Int>()

    override fun formatTitle(title: String, columnIndex: Int): String {
        if (columnIndex in 1 until columnNumber - 1) {
            return GOAL_TITLE.format(Locale.US, title, goalNameToScoreMap[title])
        }
        else if(columnIndex == columnNumber - 1) {
            return TOTAL_SCORE_TITLE.format(Locale.US, title, targetScore)
        }
        return super.formatTitle(title, columnIndex)
    }

    override fun drawCell(column: Int, row: Int, value: Any?, rowType: Int) {
        val bgColor = when(rowType) {
            1 -> COLOR_FV_GOOD
            else -> COLOR_IV_GOOD
        }
        when (column) {
            columnNumber - 1 -> {
                val totalScore = value as Int
                super.drawCell(column, row, TOTAL_SCORE.format(Locale.US, totalScore), rowType)
                if (totalScore >= targetScore) {
                    ImGui.tableSetBgColor(ImGuiTableBgTarget.CellBg, bgColor)
                }
            }
            COLUMN_T -> {
                super.drawCell(column, row, value, rowType)
            }
            else -> {
                value as Pair<*, *>
                val score = value.second as Int
                super.drawCell(column, row, CURRENT_VALUE.format(Locale.US, score), rowType)
                val achieved = value.first as Boolean
                if (achieved)
                    ImGui.tableSetBgColor(ImGuiTableBgTarget.CellBg, bgColor)
            }
        }
    }

    private companion object {
        const val TITLE_WINDOW = "Goals"
        const val TOTAL_SCORE = "%d"
        const val CURRENT_VALUE = "%d"
        const val GOAL_TITLE = "%s (%d)"
        const val TOTAL_SCORE_TITLE = "%s (%d)"
        const val COLUMN_T = 0
    }
}