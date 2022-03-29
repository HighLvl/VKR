package app.components.experiment.variables.mutable

import app.components.experiment.view.InputValuesTableView
import core.datatypes.base.Series

class MutableVariablesTableView(dataSource: Map<String, Series<Double>>) :
    InputValuesTableView(TITLE_MUTABLE_VARIABLES_WINDOW, dataSource) {

    override fun inputValue(varName: String, index: Int) {
        if (varName != "t") {
            super.inputValue(varName, index)
        }
    }

    private companion object {
        const val TITLE_MUTABLE_VARIABLES_WINDOW = "Mutable Variables"
    }
}