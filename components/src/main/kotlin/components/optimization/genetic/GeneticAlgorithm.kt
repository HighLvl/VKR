package components.optimization.genetic

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


@TargetEntity(Experimenter::class, [OptimizationExperiment::class])
class GeneticAlgorithm : Component() {
    private lateinit var optimizationExperiment: OptimizationExperiment
    private val disposables = mutableListOf<Disposable>()

    private var optimizer: Optimizer? = null

    /**The number of generations.*/
    @AddToSnapshot(0)
    var generations = 1000

    /**The size of the population.*/
    @AddToSnapshot(1)
    var alpha = 100

    /**The number of parents per generation.*/
    @AddToSnapshot(2)
    var mu = 25

    /**The number of offspring per generation.*/
    @AddToSnapshot(3)
    var lambda = 25

    /**Performs a crossover operation with this given rate.*/
    @AddToSnapshot(4)
    var crossoverRate = 0.95

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
                val ea = EvolutionaryAlgorithmModule().apply {
                    generations = this@GeneticAlgorithm.generations
                    alpha = this@GeneticAlgorithm.alpha
                    mu = this@GeneticAlgorithm.mu
                    lambda = this@GeneticAlgorithm.lambda
                    crossoverRate = this@GeneticAlgorithm.crossoverRate
                }
                optimizer = Optimizer(ea, optimizationExperiment)
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


