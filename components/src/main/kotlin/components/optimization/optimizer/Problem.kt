package components.optimization.optimizer

import core.components.experiment.OptimizationExperiment
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.runBlocking

object Problem {
    lateinit var inputParams: List<OptimizationExperiment.Input>
    lateinit var experiment: OptimizationExperiment
    private var targetFunctionChannel: Channel<Double>? = null
    private var waitDecisionChannel: Channel<Unit>? = null
    var maxStepDev = 1

    fun makeDecision(): Boolean {
        if (experiment.makeDecision()) {
            runBlocking { waitDecisionChannel!!.send(Unit) }
            return true
        }
        return false
    }

    fun getTargetFunctionValue(): Double {
        return runBlocking { targetFunctionChannel!!.receive() }
    }

    suspend fun evaluateValue(value: Double) {
        targetFunctionChannel!!.send(value)
    }

    suspend fun waitDecision() {
        waitDecisionChannel!!.receive()
    }

    fun start() {
        targetFunctionChannel = Channel()
        waitDecisionChannel = Channel()
    }

    fun close() {
        kotlin.runCatching {
            targetFunctionChannel!!.close()
        }
        kotlin.runCatching {
            waitDecisionChannel!!.close()
        }
        targetFunctionChannel = null
        waitDecisionChannel = null
    }
}