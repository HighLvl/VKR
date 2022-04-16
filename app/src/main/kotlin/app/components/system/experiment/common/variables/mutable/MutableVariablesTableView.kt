package app.components.system.experiment.common.variables.mutable

import app.components.system.experiment.common.view.InputValuesTableView
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