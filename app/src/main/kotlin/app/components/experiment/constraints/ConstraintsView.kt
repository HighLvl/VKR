package app.components.experiment.constraints

import app.components.experiment.view.*
import core.datatypes.base.MutableSeries
import core.datatypes.base.Series
import imgui.flag.ImGuiTableBgTarget
import imgui.internal.ImGui
import java.util.*

class ConstraintsView(dataSource: Map<String, Series<*>>, rowTypes: MutableSeries<Int>) :
    TableView(TITLE_CONSTRAINTS_WINDOW, dataSource, rowTypes) {

    override fun drawCell(column: Int, row: Int, value: Any?, rowType: Int) {
        if (column == 0) {
            super.drawCell(column, row, value, rowType)
            return
        }
        val color = when (rowType) {
            1 -> {
                value as Boolean
                super.drawCell(column, row, "", rowType)
                if (!value) COLOR_EV_BAD
                else COLOR_EV_GOOD
            }
            else -> {
                if (!(value as Boolean)) COLOR_IV_BAD
                else COLOR_IV_GOOD
            }
        }
        ImGui.tableSetBgColor(ImGuiTableBgTarget.CellBg, color)
    }

    private companion object {
        const val TITLE_CONSTRAINTS_WINDOW = "Constraints"
    }
}