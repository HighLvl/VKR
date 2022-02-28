package app.components.experiment.constraints

import app.components.experiment.view.TableView
import core.datatypes.base.Series
import imgui.ImColor
import imgui.flag.ImGuiTableBgTarget
import imgui.internal.ImGui

class ConstraintsView(dataSource: Map<String, Series<*>>) :
    TableView(TITLE_CONSTRAINTS_WINDOW, dataSource) {

    override fun drawCell(columnTitle: String, row: Int, value: Any?) {
        if (columnTitle == "t") {
            ImGui.text(value!!.toString())
            return
        }
        if (!(value as Boolean)) {
            ImGui.tableSetBgColor(ImGuiTableBgTarget.CellBg, ImColor.intToColor(255, 100, 100, 255))
        }
    }

    private companion object {
        const val TITLE_CONSTRAINTS_WINDOW = "Constraints"
    }
}