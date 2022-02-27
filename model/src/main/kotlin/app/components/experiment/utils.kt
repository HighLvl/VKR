package app.components.experiment

import core.datatypes.base.Series
import imgui.internal.ImGui

fun fillInTableWithData(dataSource: Map<String, Series<Float>>, varNameColumnIndexMap: Map<String, Int>) {
    val rowsNumber = dataSource.values.first().size
    for (i in 0 until rowsNumber) {
        ImGui.tableNextRow()
        dataSource.entries.forEach { (varName, values) ->
            val columnIndex = varNameColumnIndexMap[varName]!!
            ImGui.tableSetColumnIndex(columnIndex)
            ImGui.text(
                when {
                    values[i] == null -> ""
                    else -> values[i].toString()
                }
            )
        }
    }
}