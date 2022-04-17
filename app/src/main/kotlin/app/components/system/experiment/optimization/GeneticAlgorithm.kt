package app.components.system.experiment.optimization

import core.components.base.Component
import core.components.base.Script
import core.components.base.TargetEntity
import core.components.experiment.OptimizationExperiment
import core.entities.Experimenter
import core.entities.getComponent
import core.services.Services

@TargetEntity(Experimenter::class, [OptimizationExperiment::class])
class GeneticAlgorithm : Component, Script {
    private lateinit var optimizationExperiment: OptimizationExperiment
    override fun onAttach() {
        optimizationExperiment = Services.scene.experimenter.getComponent()!!
        optimizationExperiment.commandObservable.observe {
            processCommand(it)
        }
    }

    private fun processCommand(command: OptimizationExperiment.Command) {
        when (command) {
            is OptimizationExperiment.Command.Stop -> {
            }
            is OptimizationExperiment.Command.Start -> {
            }
            is OptimizationExperiment.Command.Run -> {
            }
            is OptimizationExperiment.Command.WaitDecision -> {
                command.processWaitDecisionState()
            }
        }
    }

    private fun OptimizationExperiment.Command.WaitDecision.processWaitDecisionState() {
        targetFunctionValue
        inputParams
        makeDecision()
    }
}