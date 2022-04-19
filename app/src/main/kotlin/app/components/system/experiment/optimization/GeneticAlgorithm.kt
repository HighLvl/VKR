package app.components.system.experiment.optimization

import core.components.base.Component
import core.components.base.Script
import core.components.base.TargetEntity
import core.components.experiment.OptimizationExperiment
import core.entities.Experimenter
import core.entities.getComponent
import core.services.Services
import core.utils.Disposable

@TargetEntity(Experimenter::class, [OptimizationExperiment::class])
class GeneticAlgorithm : Component, Script {
    private lateinit var optimizationExperiment: OptimizationExperiment
    private lateinit var inputParams: List<OptimizationExperiment.Input>
    private val disposables = mutableListOf<Disposable>()

    override fun onAttach() {
        optimizationExperiment = Services.scene.experimenter.getComponent()!!
        disposables += optimizationExperiment.commandObservable.observe {
            processCommand(it)
        }
    }

    override fun onDetach() {
        disposables.forEach { it.dispose() }
    }

    private fun processCommand(command: OptimizationExperiment.Command) {
        when (command) {
            is OptimizationExperiment.Command.Start -> {
                inputParams = command.inputParams
            }
            is OptimizationExperiment.Command.MakeInitialDecision -> {
                makeInitialDecision()
            }
            is OptimizationExperiment.Command.Run -> { }
            is OptimizationExperiment.Command.MakeDecision -> {
                makeDecision(command.targetFunctionValue)
            }
            is OptimizationExperiment.Command.Stop -> {
                if (command.hasGoalBeenAchieved) {

                }
            }
        }
    }

    private fun makeInitialDecision() {

    }

    private fun makeDecision(targetFunctionValue: Double){
        inputParams
        targetFunctionValue
        optimizationExperiment.makeDecision()
    }
}