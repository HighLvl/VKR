package app.components.experiment.constraints

import app.components.experiment.controller.CtrlMakeDecisionData
import app.coroutines.Contexts
import core.components.experiment.Goal
import core.datatypes.base.MutableSeries
import core.datatypes.mutableSeriesOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

class ConstraintsController(makeDecisionDataFlow: SharedFlow<CtrlMakeDecisionData>) {
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
    private lateinit var constraints: Set<Goal>
    private val coroutineScope = CoroutineScope(Contexts.app)

    init {
        coroutineScope.launch {
            makeDecisionDataFlow.collect {
                appendFinalValues(it)
            }
        }
    }

    private fun appendFinalValues(makeDecisionData: CtrlMakeDecisionData) = with(constraintSeries){
//        get("t")!!.append("FV")
//        makeDecisionCondition.constraintValues.forEach { entry ->
//            get(entry.key)!!.append(entry.value)
//        }
//        rowTypes.append(1)
    }


    fun reset(constraints: Set<Goal>) {
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
            constraintValues[it.name] = it.valueHolder.instantValue
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