package app.components.experiment.variables.mutable

import app.components.experiment.view.TableView
import core.datatypes.base.Series
import imgui.flag.ImGuiInputTextFlags
import imgui.internal.ImGui
import imgui.type.ImDouble

class MutableVariablesView(private val dataSource: Map<String, Series<Double>>) :
    TableView(TITLE_MUTABLE_VARIABLES_WINDOW, dataSource) {
    var onChangeValueListener: (String, Double) -> Unit = { _, _ -> }
    private val varValueImDoubles = mutableMapOf<String, ImDouble>()

    override fun reset() {
        super.reset()
        dataSource.keys.forEach { varName ->
            varValueImDoubles[varName] = ImDouble()
        }
    }

    override fun fillInTableWithData() {
        inputValues()
        super.fillInTableWithData()
    }

    private fun inputValues() {
        ImGui.tableNextRow()
        nameIndexMap.entries.forEach { (varName, index) ->
            ImGui.tableSetColumnIndex(index)
            if (varName != "t") {
                val valueImDouble = varValueImDoubles[varName]!!
                ImGui.pushID(index)
                if (ImGui.inputDouble("", valueImDouble, 0.0, 0.0, "%g", ImGuiInputTextFlags.EnterReturnsTrue)) {
                    onChangeValueListener(varName, valueImDouble.get())
                }
                ImGui.popID()
            }
        }
    }

    private companion object {
        const val TITLE_MUTABLE_VARIABLES_WINDOW = "Mutable Variables"
    }
}