package core.components.experiment

import core.utils.ValueObservable

interface Experiment {
    val taskModelObservable: ValueObservable<ExperimentTaskModel>
}

typealias GetterExp = () -> Double
typealias SetterExp = (Double) -> Unit
typealias PredicateExp = () -> Boolean

interface ValueHolder<T> {
    val value: T
}

data class Goal(val name: String, val score: Int, val valueHolder: ValueHolder<Boolean>) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Goal

        if (name != other.name) return false

        return true
    }

    override fun hashCode(): Int {
        return name.hashCode()
    }
}

data class InputParam(
    val name: String,
    val initialValue: Double,
    val minValue: Double,
    val maxValue: Double,
    val step: Double,
    val setter: SetterExp
)