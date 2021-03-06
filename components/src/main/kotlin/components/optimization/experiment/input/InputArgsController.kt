package components.optimization.experiment.input

import core.components.experiment.OptimizationExperiment
import core.datatypes.base.MutableSeries
import core.datatypes.mutableSeriesOf
import core.services.logger.Level
import core.services.logger.Logger

internal class InputArgsController {
    private val inputParamsSeries = mutableMapOf<String, MutableSeries<Any>>()
    private val mutableVariablesView = InputArgsView(inputParamsSeries).apply {
        onChangeValueListener = { varName, value ->
            inputParams[varName]!!.value = value
        }
    }
    private lateinit var inputParams: Map<String, OptimizationExperiment.Input>
    private var newInputParams = mutableMapOf<String, Double>()

    var enabled: Boolean by mutableVariablesView::enabled

    private var decisionsNumber = 0

    var trackedDataSize = Int.MAX_VALUE
        set(value) {
            field = value
            inputParamsSeries.entries.forEach { (_, series) -> series.capacity = value }
        }

    fun reset(inputParams: Map<String, OptimizationExperiment.Input>) {
        this.inputParams = inputParams
        inputParamsSeries.clear()
        inputParamsSeries["#"] = mutableSeriesOf(trackedDataSize)
        decisionsNumber = 0
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
        decisionsNumber++
        inputParamsSeries["#"]!!.append(decisionsNumber)
        inputParams.forEach { (varName, param) ->
            inputParamsSeries[varName]!!.append(newInputParams[varName] ?: param.value)
        }
        newInputParams.clear()
    }
}