package app.components.experiment

import core.datatypes.base.MutableSeries
import imgui.flag.ImGuiInputTextFlags
import imgui.flag.ImGuiTableColumnFlags
import imgui.flag.ImGuiTableFlags
import imgui.internal.ImGui
import imgui.type.ImBoolean
import imgui.type.ImFloat

class MutableVariablesView(private val dataSource: MutableMap<String, MutableSeries<Float>>) {
    private var docked = false
    private val nameIndexMap = mutableMapOf<String, Int>()
    var enabled
        set(value) {
            showMutableVariablesImBoolean.set(value)
        }
        get() = showMutableVariablesImBoolean.get()
    private val showMutableVariablesImBoolean = ImBoolean(false)
    var onChangeValueListener: (String, Float) -> Unit = { _, _ -> }
    private val varValueImFloats = mutableMapOf<String, ImFloat>()

    fun reset() {
        nameIndexMap.clear()
        dataSource.entries.forEachIndexed() { index, (varName, _) ->
            nameIndexMap[varName] = index
            varValueImFloats[varName] = ImFloat()
        }
    }

    fun update() {
        showOpenedMutableVariables()
    }

    private fun showOpenedMutableVariables() {
        if (showMutableVariablesImBoolean.get()) {
            showMutableVariables()
            if (!docked) {
                ImGui.dockBuilderDockWindow(TITLE_MUTABLE_VARIABLES_WINDOW, ImGui.getWindowDockID())
                docked = true
            }
        }
    }

    private fun showMutableVariables() {
        if (ImGui.begin(TITLE_MUTABLE_VARIABLES_WINDOW, showMutableVariablesImBoolean)) {
            val columnsNumber = dataSource.keys.size
            if (columnsNumber > 0) {
                ImGui.beginTable(ID_MUTABLE_VARIABLES_TABLE, columnsNumber, ImGuiTableFlags.Borders)
                setupObservableVarTableHeader()
                inputValues()
                fillInTableWithData(dataSource, nameIndexMap)
                ImGui.endTable()
            }
        }
        ImGui.end()
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

    private fun inputValues() {
        ImGui.tableNextRow()
        nameIndexMap.entries.forEach { (varName, index) ->
            ImGui.tableSetColumnIndex(index)
            when (varName) {
                "t" -> {
                }
                else -> {
                    val valueImFloat = varValueImFloats[varName]!!
                    ImGui.pushID(index)
                    if (ImGui.inputFloat(
                            "", valueImFloat, 0f, 0f, "%g",
                            ImGuiInputTextFlags.EnterReturnsTrue
                        )
                    ) {
                        onChangeValueListener(varName, valueImFloat.get())
                    }
                    ImGui.popID()
                }
            }

        }
    }

    private companion object {
        const val TITLE_MUTABLE_VARIABLES_WINDOW = "Mutable Variables"
        const val ID_MUTABLE_VARIABLES_TABLE = "mutable_table_id"
    }
}