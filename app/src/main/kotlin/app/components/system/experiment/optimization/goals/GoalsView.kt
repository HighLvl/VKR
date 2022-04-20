package app.components.system.experiment.optimization.goals

import app.components.system.experiment.common.view.COLOR_FV_GOOD
import app.components.system.experiment.common.view.COLOR_IV_GOOD
import app.components.system.experiment.common.view.TableView
import app.utils.getString
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
            return getString("goal_title", Locale.US, title, goalNameToScoreMap[title])
        }
        else if(columnIndex == columnNumber - 1) {
            return getString("total_score_title", Locale.US, title, targetScore)
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
                super.drawCell(column, row, getString("total_score", Locale.US, totalScore), rowType)
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
                super.drawCell(column, row, getString("current_value", Locale.US, score), rowType)
                val achieved = value.first as Boolean
                if (achieved)
                    ImGui.tableSetBgColor(ImGuiTableBgTarget.CellBg, bgColor)
            }
        }
    }

    private companion object {
        val TITLE_WINDOW = getString("goals_window_title")
        const val COLUMN_T = 0
    }
}