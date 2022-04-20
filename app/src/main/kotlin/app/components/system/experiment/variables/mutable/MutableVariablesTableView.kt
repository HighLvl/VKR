package app.components.system.experiment.variables.mutable

import app.components.system.experiment.common.view.InputValuesTableView
import app.utils.getString
import core.datatypes.base.Series

class MutableVariablesTableView(dataSource: Map<String, Series<Double>>) :
    InputValuesTableView(TITLE_MUTABLE_VARIABLES_WINDOW, dataSource) {

    override fun inputValue(varName: String, index: Int) {
        if (varName != "t") {
            super.inputValue(varName, index)
        }
    }

    private companion object {
        val TITLE_MUTABLE_VARIABLES_WINDOW = getString("mut_vars_window_title")
    }
}