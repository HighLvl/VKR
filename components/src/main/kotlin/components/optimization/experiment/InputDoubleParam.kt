package components.optimization.experiment

import core.components.experiment.OptimizationExperiment
import kotlin.properties.Delegates

internal class InputDoubleParam(
    override val name: String,
    override val minValue: Double,
    override val maxValue: Double,
    override val step: Double,
    onValueChanged: (Double) -> Unit
) : OptimizationExperiment.Input {
    override var value: Double by Delegates.observable(0.0) { _, _, newValue ->
        onValueChanged(newValue)
    }
}