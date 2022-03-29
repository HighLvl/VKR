package core.components.experiment

import core.components.base.Component
import kotlinx.coroutines.flow.Flow

interface DoubleParam {
    var value: Double
}

interface Experiment : Component {
    val inputParams: List<DoubleParam>
    val modelRunResultFlow: Flow<ModelRunResult>
}

data class ModelRunResult(val targetFunctionValues: List<Double>, val isGoalAchieved: Boolean)


typealias GetterExp = () -> Double
typealias SetterExp = (Double) -> Unit
typealias PredicateExp = () -> Boolean
typealias TargetFunction = () -> Double

data class Goal(val name: String, val rating: Double, val targetFunction: TargetFunction)
data class Predicate(val name: String, val predicateExp: PredicateExp) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Predicate

        if (name != other.name) return false

        return true
    }

    override fun hashCode(): Int {
        return name.hashCode()
    }
}
data class InputParam(val name: String, val initialValue: Double, val setter: SetterExp)