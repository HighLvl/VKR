package app.components.system.experiment.common.input

import app.components.system.experiment.common.view.InputValuesTableView
import app.utils.getString
import core.datatypes.base.Series

class InputArgsView(dataSource: Map<String, Series<Double>>) :
    InputValuesTableView(TITLE_INPUT_ARGS_WINDOW, dataSource) {

    private companion object {
        val TITLE_INPUT_ARGS_WINDOW = getString("input_args_window_title")
    }
}