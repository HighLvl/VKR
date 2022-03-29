package app.components.experiment.input

import app.components.experiment.view.InputValuesTableView
import core.datatypes.base.Series

class InputArgsView(dataSource: Map<String, Series<Double>>) :
    InputValuesTableView(TITLE_INPUT_ARGS_WINDOW, dataSource) {

    private companion object {
        const val TITLE_INPUT_ARGS_WINDOW = "Input Args"
    }
}