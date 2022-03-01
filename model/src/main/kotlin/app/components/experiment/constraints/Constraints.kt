package app.components.experiment.constraints

import app.components.experiment.ExperimentTaskModel
import app.components.experiment.MutableExperimentTaskModel
import core.datatypes.base.MutableSeries
import core.datatypes.mutableSeriesOf

class Constraints {
    var trackedDataSize = Int.MAX_VALUE
        set(value) {
            field = value
            constraintSeries.entries.forEach { (_, series) -> series.capacity = value }
        }

    var enabled: Boolean
        set(value) {
            constraintsView.enabled = value
        }
        get() = constraintsView.enabled

    private val constraintSeries = mutableMapOf<String, MutableSeries<Any>>()
    private val constraintsView = ConstraintsView(constraintSeries)
    private val constraintValues = mutableMapOf<String, Boolean>()
    private lateinit var constraints: Set<ExperimentTaskModel.Predicate>

    fun reset(constraints: Set<ExperimentTaskModel.Predicate>) {
        this.constraints = constraints
        constraintSeries.clear()
        constraintValues.clear()
        constraintSeries["t"] = mutableSeriesOf(trackedDataSize)
        this.constraints.forEach {
            constraintSeries[it.name] = mutableSeriesOf(trackedDataSize)
        }
        constraintsView.reset()
    }

    fun onModelUpdate(modelTime: Float) {
        constraints.forEach {
            constraintValues[it.name] = it.predicateFun()
        }
        val prevConstraintValues = constraintSeries.entries.asSequence()
            .filterNot { it.key == "t" }.map {
                it.key to it.value.last
            }.toMap()
        if (prevConstraintValues != constraintValues) {
            constraintSeries["t"]!!.append(modelTime)
            constraintValues.forEach { constraintSeries[it.key]!!.append(it.value) }
        }
    }

    fun update() {
        constraintsView.update()
    }
}