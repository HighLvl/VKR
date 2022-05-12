package components.optimization.experiment.input

import components.view.InputValuesTableView
import core.utils.getString
import core.datatypes.base.Series
import imgui.internal.ImGui

internal class InputArgsView(dataSource: Map<String, Series<Any>>) :
    InputValuesTableView(TITLE_INPUT_ARGS_WINDOW, dataSource) {

    override fun inputValues() {
        ImGui.tableNextRow()
        indexNameMap.entries.forEach { (index, varName) ->
            ImGui.tableSetColumnIndex(index)
            if (index == 0) return@forEach
            inputValue(varName, index)
        }
    }

    private companion object {
        val TITLE_INPUT_ARGS_WINDOW = getString("input_args_window_title")
    }
}