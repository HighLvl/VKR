package components.view

import core.datatypes.base.Series
import imgui.flag.ImGuiInputTextFlags
import imgui.internal.ImGui
import imgui.type.ImDouble

open class InputValuesTableView(title: String, private val dataSource: Map<String, Series<Double>>) :
    TableView(title, dataSource) {
    var onChangeValueListener: (String, Double) -> Unit = { _, _ -> }
    private val varValueImDoubles = mutableMapOf<String, ImDouble>()
    private var initialValues = mapOf<String, Double>()


    fun setInputValues(initialValues: Map<String, Double>) {
        this.initialValues = initialValues
        initialValues.forEach {
            varValueImDoubles[it.key]!!.set(it.value)
        }
    }

    override fun reset() {
        super.reset()
        varValueImDoubles.clear()
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
        indexNameMap.entries.forEach { (index, varName) ->
            ImGui.tableSetColumnIndex(index)
            inputValue(varName, index)
        }
    }

    open fun inputValue(varName: String, index: Int) {
        val valueImDouble = varValueImDoubles[varName]!!
        ImGui.pushID(index)
        if (ImGui.inputDouble("", valueImDouble, 0.0, 0.0, "%g", ImGuiInputTextFlags.EnterReturnsTrue)) {
            onChangeValueListener(varName, valueImDouble.get())
        }
        ImGui.popID()
    }
}