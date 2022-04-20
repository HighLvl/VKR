package app.components.system.experiment.optimization.input

import core.components.experiment.OptimizationExperiment
import core.datatypes.base.MutableSeries
import core.datatypes.mutableSeriesOf
import core.services.logger.Level
import core.services.logger.Logger

class InputArgsController {
    private val inputParamsSeries = mutableMapOf<String, MutableSeries<Double>>()
    private val mutableVariablesView = InputArgsView(inputParamsSeries).apply {
        onChangeValueListener = { varName, value ->
            inputParams[varName]!!.value = value
        }
    }
    private lateinit var inputParams: Map<String, OptimizationExperiment.Input>
    private var newInputParams = mutableMapOf<String, Double>()

    var enabled: Boolean by mutableVariablesView::enabled

    var trackedDataSize = Int.MAX_VALUE
        set(value) {
            field = value
            inputParamsSeries.entries.forEach { (_, series) -> series.capacity = value }
        }

    fun reset(inputParams: Map<String, OptimizationExperiment.Input>) {
        this.inputParams = inputParams
        inputParamsSeries.clear()
        inputParams.keys.forEach { varName ->
            inputParamsSeries[varName] = mutableSeriesOf(trackedDataSize)
        }
        mutableVariablesView.reset()
        newInputParams.clear()
    }

    fun update() {
        mutableVariablesView.update()
    }

    fun onChangeInputParamValue(varName: String, value: Double) {
        Logger.log("\"$varName\"=$value", Level.DEBUG)
        newInputParams[varName] = value
        mutableVariablesView.setInputValues(newInputParams.toMap())
    }

    fun commitInputArgs() {
        inputParams.forEach { (varName, param) ->
            inputParamsSeries[varName]!!.append(newInputParams[varName] ?: param.value)
        }
        newInputParams.clear()
    }
}