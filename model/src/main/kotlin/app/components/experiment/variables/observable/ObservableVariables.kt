package app.components.experiment.variables.observable

import core.datatypes.base.MutableSeries
import core.datatypes.mutableSeriesOf

class ObservableVariables {
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
    private val observableVariablesSeries = mutableMapOf<String, MutableSeries<Float>>()
    private val observableVariablesView = ObservableVariablesView(observableVariablesSeries)
    private lateinit var observableVars: Map<String, () -> Float>

    fun reset(observableVariables: Map<String, () -> Float>) {
        observableVars = observableVariables
        observableVariablesSeries.clear()
        observableVariablesSeries["t"] = mutableSeriesOf(trackedDataSize)
        observableVars.entries.forEach { (varName, _) ->
            observableVariablesSeries[varName] = mutableSeriesOf(trackedDataSize)
        }
        observableVariablesView.reset()
    }

    fun onModelUpdate(modelTime: Float) {
        observableVars.forEach { (varName, getValue) ->
            observableVariablesSeries[varName]!!.append(getValue())
        }
        observableVariablesSeries["t"]!!.append(modelTime)
    }

    fun update() {
        observableVariablesView.update()
    }
}