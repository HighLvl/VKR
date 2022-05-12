package components.optimization.optimizer

import components.optimization.opt4j.DoubleProblemModule
import components.optimization.opt4j.NeighbourModule
import core.components.experiment.OptimizationExperiment
import core.coroutines.Contexts
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.launch
import org.opt4j.core.optimizer.OptimizerModule
import org.opt4j.core.start.Opt4JTask
import org.opt4j.viewer.ViewerModule
import java.io.Closeable

class Optimizer(optimizerModule: OptimizerModule, private val optimizationExperiment: OptimizationExperiment) :
    Closeable {
    private val task = Opt4JTask()
    private val coroutineScope = CoroutineScope(Dispatchers.IO)
    private val waitDecisionScope = CoroutineScope(Contexts.app)
    private var closed = false

    init {
        task.init(optimizerModule, DoubleProblemModule(), ViewerModule(), NeighbourModule())
        Problem.experiment = optimizationExperiment
    }

    override fun close() {
        if (closed) return
        task.close()
        Problem.close()
        coroutineScope.coroutineContext.cancelChildren()
        waitDecisionScope.coroutineContext.cancelChildren()
        closed = true
        optimizationExperiment.stop()
    }

    fun start(inputParams: List<OptimizationExperiment.Input>) {
        Problem.inputParams = inputParams
        Problem.start()
    }

    private fun launchOpt4J() {
        coroutineScope.coroutineContext.cancelChildren()
        coroutineScope.launch {
            runEvolutionaryAlgorithm()
        }
    }

    private suspend fun launchAndJoin(block: suspend CoroutineScope.() -> Unit) {
        kotlin.runCatching { waitDecisionScope.launch { block() }.join() }
    }

    private fun runEvolutionaryAlgorithm() {
        try {
            task.execute()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            close()
        }
    }

    suspend fun makeInitialDecision() {
        launchOpt4J()
        launchAndJoin {
            Problem.waitInitialDecision()
        }
    }

    suspend fun makeDecision(tfValue: Double) {
        launchAndJoin {
            Problem.waitDecision(tfValue)
        }
    }
}