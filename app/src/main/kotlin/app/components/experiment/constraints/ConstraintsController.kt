package app.components.experiment.constraints

import app.components.experiment.controller.ModelRunResult
import app.coroutines.Contexts
import core.components.experiment.Predicate
import core.datatypes.base.MutableSeries
import core.datatypes.mutableSeriesOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

class ConstraintsController(modelRunResultFlow: SharedFlow<ModelRunResult>) {
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
    private val rowTypes = mutableSeriesOf<Int>()
    private val constraintsView = ConstraintsView(constraintSeries, rowTypes)
    private val constraintValues = mutableMapOf<String, Boolean>()
    private lateinit var constraints: Set<Predicate>
    private val coroutineScope = CoroutineScope(Contexts.app)

    init {
        coroutineScope.launch {
            modelRunResultFlow.collect {
                appendExpectedValues(it)
            }
        }
    }

    private fun appendExpectedValues(modelRunResult: ModelRunResult) = with(constraintSeries){
        get("t")!!.append("EV")
        modelRunResult.constraintExpectedValues.forEach { entry ->
            get(entry.key)!!.append(entry.value * 100)
        }
        rowTypes.append(1)
    }


    fun reset(constraints: Set<Predicate>) {
        this.constraints = constraints
        constraintValues.clear()
        with(constraintSeries) {
            clear()
            put("t", mutableSeriesOf(trackedDataSize))
            constraints.forEach {
                put(it.name, mutableSeriesOf(trackedDataSize))
            }
        }
        constraintsView.reset()
    }

    fun onModelUpdate(modelTime: Double) {
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
            rowTypes.append(0)
        }
    }

    fun update() {
        constraintsView.update()
    }
}