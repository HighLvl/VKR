package app.components.system.experiment.variables.mutable

import core.services.logger.Logger
import core.datatypes.base.MutableSeries
import core.datatypes.mutableSeriesOf
import core.services.logger.Level

class MutableVariablesController {
    var enabled: Boolean
        get() = mutableVariablesView.enabled
        set(value) {
            mutableVariablesView.enabled = value
        }
    var trackedDataSize = Int.MAX_VALUE
        set(value) {
            field = value
            mutableVariablesSeries.entries.forEach { (_, series) -> series.capacity = value }
        }

    private val mutableVariablesSeries = mutableMapOf<String, MutableSeries<Double>>()
    private val mutableVariablesView = MutableVariablesTableView(mutableVariablesSeries)
        .apply { onChangeValueListener = ::onChangeMutableVarValue }
    private val mutableVarValues = mutableMapOf<String, Double>()
    private lateinit var mutableVars: Map<String, (Double) -> Unit>

    fun reset(mutableVariables: Map<String, (Double) -> Unit>) {
        mutableVars = mutableVariables
        mutableVariablesSeries.clear()
        mutableVariablesSeries["t"] = mutableSeriesOf(trackedDataSize)
        mutableVars.keys.forEach { varName ->
            mutableVariablesSeries[varName] = mutableSeriesOf(trackedDataSize)
        }
        mutableVariablesView.reset()
        mutableVarValues.clear()
    }

    fun onModelUpdate(modelTime: Double) {
        if (mutableVarValues.isEmpty()) return
        mutableVariablesSeries["t"]!!.append(modelTime)
        mutableVariablesSeries.asSequence().filterNot { it.key == "t" }.forEach { (varName, series) ->
            val mutableVarValue = mutableVarValues[varName]
            if (mutableVarValue != null) {
                mutableVars[varName]!!.invoke(mutableVarValue)
            }
            series.append(mutableVarValue)
        }
        mutableVarValues.clear()
    }

    fun update() {
        mutableVariablesView.update()
    }

    private fun onChangeMutableVarValue(varName: String, value: Double) {
        Logger.log("Set $varName to $value", Level.DEBUG)
        mutableVarValues[varName] = value
    }
}