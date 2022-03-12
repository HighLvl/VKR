package app.components.experiment.variables.mutable

import app.components.experiment.view.TableView
import core.datatypes.base.Series
import imgui.flag.ImGuiInputTextFlags
import imgui.internal.ImGui
import imgui.type.ImFloat

class MutableVariablesView(private val dataSource: Map<String, Series<Float>>) :
    TableView(TITLE_MUTABLE_VARIABLES_WINDOW, dataSource) {
    var onChangeValueListener: (String, Float) -> Unit = { _, _ -> }
    private val varValueImFloats = mutableMapOf<String, ImFloat>()

    override fun reset() {
        super.reset()
        dataSource.keys.forEach { varName ->
            varValueImFloats[varName] = ImFloat()
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
                val valueImFloat = varValueImFloats[varName]!!
                ImGui.pushID(index)
                if (ImGui.inputFloat("", valueImFloat, 0f, 0f, "%g", ImGuiInputTextFlags.EnterReturnsTrue)) {
                    onChangeValueListener(varName, valueImFloat.get())
                }
                ImGui.popID()
            }
        }
    }

    private companion object {
        const val TITLE_MUTABLE_VARIABLES_WINDOW = "Mutable Variables"
    }
}