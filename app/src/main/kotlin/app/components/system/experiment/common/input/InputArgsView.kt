package app.components.system.experiment.common.input

import app.components.system.experiment.common.view.InputValuesTableView
import core.datatypes.base.Series

class InputArgsView(dataSource: Map<String, Series<Double>>) :
    InputValuesTableView(TITLE_INPUT_ARGS_WINDOW, dataSource) {

    private companion object {
        const val TITLE_INPUT_ARGS_WINDOW = "Input Args"
    }
}