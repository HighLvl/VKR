package components.optimization.annealing

import components.optimization.optimizer.Problem
import components.optimization.optimizer.Optimizer
import core.components.base.AddToSnapshot
import core.components.base.Component
import core.components.base.TargetEntity
import core.components.experiment.OptimizationExperiment
import core.entities.Experimenter
import core.entities.getComponent
import core.services.Services
import core.utils.Disposable
import org.opt4j.optimizers.ea.EvolutionaryAlgorithmModule
import org.opt4j.optimizers.sa.SimulatedAnnealingModule


@TargetEntity(Experimenter::class, [OptimizationExperiment::class])
class SimulatedAnnealingAlgorithm : Component() {
    private lateinit var optimizationExperiment: OptimizationExperiment
    private val disposables = mutableListOf<Disposable>()

    private var optimizer: Optimizer? = null

    /**The number of iterations.*/
    @AddToSnapshot(0)
    var iterations = 100000

    @AddToSnapshot(1)
    var maxStepDev by Problem::maxStepDev

    override fun onAttach() {
        optimizationExperiment = Services.scene.experimenter.getComponent()!!
        disposables += optimizationExperiment.commandObservable.observe {
            processCommand(it)
        }
        Problem.experiment = optimizationExperiment
    }

    override fun onDetach() {
        disposables.forEach { it.dispose() }
        optimizer?.close()
    }

    private suspend fun processCommand(command: OptimizationExperiment.Command) {
        when (command) {
            is OptimizationExperiment.Command.Start -> {
                val sa = SimulatedAnnealingModule().apply {
                    iterations = this@SimulatedAnnealingAlgorithm.iterations
                }
                optimizer = Optimizer(sa, optimizationExperiment)
                optimizer?.start(command.inputParams)
            }
            is OptimizationExperiment.Command.MakeInitialDecision -> {
                optimizer?.makeInitialDecision()
            }
            is OptimizationExperiment.Command.Run -> {
            }
            is OptimizationExperiment.Command.MakeDecision -> {
                optimizer?.makeDecision(command.targetFunctionValue)
            }
            is OptimizationExperiment.Command.Stop -> {
                optimizer?.close()
                optimizer = null
            }
        }
    }
}


