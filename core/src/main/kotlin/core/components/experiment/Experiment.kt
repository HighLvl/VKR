package core.components.experiment

import core.components.base.Component
import core.utils.ValueObservable

interface Experiment : Component {
    val taskModelObservable: ValueObservable<ExperimentTaskModel>
}

interface OptimizationExperiment : Component {
    sealed interface Command {
        data class Start(val inputParams: List<Input>) : Command
        data class Stop(val hasGoalBeenAchieved: Boolean) : Command
        object Run : Command
        interface MakeInitialDecision : Command {
            fun commit(): Boolean
        }
        interface MakeDecision : Command {
            val targetFunctionValue: Double
            fun commit(): Boolean
        }
    }

    interface Input {
        var value: Double
        val name: String
        val minValue: Double
        val maxValue: Double
        val step: Double
    }

    val commandObservable: ValueObservable<Command>
    fun start()
    fun stop()
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