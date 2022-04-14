package core.components.experiment

import core.components.base.Component
import kotlinx.coroutines.flow.Flow



interface OptimizationExperiment : Component {
    val inputParams: List<DoubleParam>
    val makeDecisionConditionFlow: Flow<MakeDecisionCondition>
    val stopOptimizationFlow: Flow<String>

    fun makeDecision()
}

interface DoubleParam {
    var value: Double
}

data class MakeDecisionCondition(val targetFunctionValues: List<Double>, val isGoalAchieved: Boolean)


typealias GetterExp = () -> Double
typealias SetterExp = (Double) -> Unit
typealias PredicateExp = () -> Boolean
typealias TargetFunction = () -> Double


interface ValueHolder<T> {
    /**
     *  @return итоговое значение, в зависиммости от которого принимается оптимизационное решение.
     *  Может зависеть от набора значений instantValue
     */
    val value: T
    /**
     *  @return мгновенное значение, зависит от снимка состояния модели. Мгновенное значение протоколируется в таблице,
     *  доступной экспериментатору для наблюдения
     */
    val instantValue: T
}

data class Goal(val name: String, val rating: Double, val targetFunctionVH: ValueHolder<Double>)
data class Predicate(val name: String, val valueHolder: ValueHolder<Boolean>) {
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