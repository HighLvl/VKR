package app.components.experiment.variables.observable

import core.datatypes.base.MutableSeries
import core.datatypes.mutableSeriesOf

class ObservableVariablesController {
    var trackedDataSize = Int.MAX_VALUE
        set(value) {
            field = value
            observableVariablesSeries.entries.forEach { (_, series) -> series.capacity = value }
        }

    var enabled: Boolean
        set(value) {
            observableVariablesView.enabled = value
        }
        get() = observableVariablesView.enabled
    private val observableVariablesSeries = mutableMapOf<String, MutableSeries<Double>>()
    private val observableVariablesView = ObservableVariablesView(observableVariablesSeries)
    private lateinit var observableVars: Map<String, () -> Double>

    fun reset(observableVariables: Map<String, () -> Double>) {
        observableVars = observableVariables
        observableVariablesSeries.clear()
        observableVariablesSeries["t"] = mutableSeriesOf(trackedDataSize)
        observableVars.entries.forEach { (varName, _) ->
            observableVariablesSeries[varName] = mutableSeriesOf(trackedDataSize)
        }
        observableVariablesView.reset()
    }

    fun onModelUpdate(modelTime: Double) {
        observableVars.forEach { (varName, getValue) ->
            observableVariablesSeries[varName]!!.append(getValue())
        }
        observableVariablesSeries["t"]!!.append(modelTime)
    }

    fun update() {
        observableVariablesView.update()
    }
}