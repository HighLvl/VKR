package core.components.experiment

import core.components.base.Component
import core.components.base.TargetEntity
import core.entities.Experimenter
import core.utils.ValueObservable

interface Experiment : Component {
    val taskModelObservable: ValueObservable<ExperimentTaskModel>
}

interface OptimizationExperiment : Component {
    sealed interface Command {
        object Start: Command
        object Stop: Command
        object Run: Command

        interface WaitDecision: Command {
            val inputParams: List<Input>
            val targetFunctionValue: Double
            fun makeDecision(): Boolean
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

interface DoubleParam {
    var value: Double
    val inputParam: InputParam
}

typealias GetterExp = () -> Double
typealias SetterExp = (Double) -> Unit
typealias PredicateExp = () -> Boolean
typealias TargetFunction = () -> Double


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