package app.components.experiment.constraints

import core.datatypes.base.Series
import imgui.ImColor
import imgui.flag.*
import imgui.internal.ImGui
import imgui.type.ImBoolean

class ConstraintsView(private val dataSource: Map<String, Series<*>>) {
    private var docked = false
    private val nameIndexMap = mutableMapOf<String, Int>()
    var enabled
        set(value) {
            showConstraintsImBoolean.set(value)
        }
        get() = showConstraintsImBoolean.get()
    private val showConstraintsImBoolean = ImBoolean(false)

    fun reset() {
        nameIndexMap.clear()
        dataSource.entries.forEachIndexed() { index, (varName, _) ->
            nameIndexMap[varName] = index
        }
    }

    fun update() {
        showOpenedConstraints()
    }

    private fun showOpenedConstraints() {
        if (showConstraintsImBoolean.get()) {
            showConstraints()
            if (!docked) {
                ImGui.dockBuilderDockWindow(TITLE_CONSTRAINTS_WINDOW, ImGui.getWindowDockID())
                docked = true
            }
        }
    }

    private fun showConstraints() {
        if (ImGui.begin(TITLE_CONSTRAINTS_WINDOW, showConstraintsImBoolean)) {
            val columnsNumber = dataSource.keys.size
            if (columnsNumber > 0) {
                ImGui.beginTable(ID_MUTABLE_VARIABLES_TABLE, columnsNumber, ImGuiTableFlags.Borders)
                setupObservableVarTableHeader()
                fillInTableWithData()
                ImGui.endTable()
            }
        }
        ImGui.end()
    }

    private fun fillInTableWithData() {
        val rowsNumber = dataSource.values.first().size
        for (i in 0 until rowsNumber) {
            ImGui.tableNextRow()
            for ((varName, values) in dataSource.entries) {
                val columnIndex = nameIndexMap[varName]!!
                ImGui.tableSetColumnIndex(columnIndex)
                if (varName == "t") {
                    ImGui.text(
                        when {
                            values[i] == null -> ""
                            else -> values[i].toString()
                        }
                    )
                    continue
                }
                if (!(values[i] as Boolean)) {
                    ImGui.tableSetBgColor(ImGuiTableBgTarget.CellBg, ImColor.intToColor(255, 100, 100, 255))
                }
            }
        }
    }

    private fun setupObservableVarTableHeader() {
        nameIndexMap.entries.forEach { (varName, index) ->
            ImGui.tableSetupColumn(
                varName,
                ImGuiTableColumnFlags.None,
                0f,
                index
            )
        }
        ImGui.tableHeadersRow()
    }

    private companion object {
        const val TITLE_CONSTRAINTS_WINDOW = "Constraints"
        const val ID_MUTABLE_VARIABLES_TABLE = "constraint_table_id"
    }
}