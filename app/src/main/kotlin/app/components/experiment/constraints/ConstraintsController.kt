package app.components.experiment.constraints

import app.components.experiment.ExperimentTaskModel
import core.components.experiment.Predicate
import core.datatypes.base.MutableSeries
import core.datatypes.mutableSeriesOf

class ConstraintsController {
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
    private lateinit var constraints: Set<Predicate>

    fun reset(constraints: Set<Predicate>) {
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
            constraintValues[it.name] = it.predicateExp()
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