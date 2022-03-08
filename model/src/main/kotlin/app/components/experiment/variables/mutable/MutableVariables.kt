package app.components.experiment.variables.mutable

import core.services.logger.Logger
import core.datatypes.base.MutableSeries
import core.datatypes.mutableSeriesOf
import core.services.logger.Level

class MutableVariables {
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

    private val mutableVariablesSeries = mutableMapOf<String, MutableSeries<Float>>()
    private val mutableVariablesView = MutableVariablesView(mutableVariablesSeries)
        .apply { onChangeValueListener = ::onChangeMutableVarValue }
    private val mutableVarValues = mutableMapOf<String, Float>()
    private lateinit var mutableVars: Map<String, (Float) -> Unit>

    fun reset(mutableVariables: Map<String, (Float) -> Unit>) {
        mutableVars = mutableVariables
        mutableVariablesSeries.clear()
        mutableVariablesSeries["t"] = mutableSeriesOf(trackedDataSize)
        mutableVars.keys.forEach { varName ->
            mutableVariablesSeries[varName] = mutableSeriesOf(trackedDataSize)
        }
        mutableVariablesView.reset()
        mutableVarValues.clear()
    }

    fun onModelUpdate(modelTime: Float) {
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

    private fun onChangeMutableVarValue(varName: String, value: Float) {
        Logger.log("Set $varName to $value", Level.DEBUG)
        mutableVarValues[varName] = value
    }
}