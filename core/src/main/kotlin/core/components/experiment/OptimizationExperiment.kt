package core.components.experiment

import core.utils.ValueObservable

interface OptimizationExperiment {
    sealed interface Command {
        data class Start(val inputParams: List<Input>) : Command
        data class Stop(val hasGoalBeenAchieved: Boolean) : Command
        object Run : Command
        object MakeInitialDecision : Command
        data class MakeDecision(val targetFunctionValue: Double) : Command
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
    fun makeDecision(): Boolean
    fun stop()
}