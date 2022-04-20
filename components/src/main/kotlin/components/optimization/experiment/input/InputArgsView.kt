package components.optimization.experiment.input

import components.view.InputValuesTableView
import core.utils.getString
import core.datatypes.base.Series

internal class InputArgsView(dataSource: Map<String, Series<Double>>) :
    InputValuesTableView(TITLE_INPUT_ARGS_WINDOW, dataSource) {

    private companion object {
        val TITLE_INPUT_ARGS_WINDOW = getString("input_args_window_title")
    }
}