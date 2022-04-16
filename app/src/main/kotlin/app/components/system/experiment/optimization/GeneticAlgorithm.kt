package app.components.system.experiment.optimization

import core.components.base.Component
import core.components.base.Script
import core.components.experiment.OptimizationExperiment
import core.entities.getComponent
import core.entities.removeComponent
import core.services.Services
import core.services.logger.Level
import core.services.logger.Logger

class GeneticAlgorithm: Component, Script {
    private lateinit var optimizationExperiment: OptimizationExperiment
    override fun onAttach() {
        try {
            optimizationExperiment = Services.scene.experimenter.getComponent()!!
        } catch (e: NullPointerException) {
            Services.scene.findEntityByComponent(this)!!.removeComponent(this::class)
            Logger.log("Optimization Experiment required", Level.ERROR)
        }
    }

    override fun onModelAfterUpdate() {
        when(val state = optimizationExperiment.state) {
            is OptimizationExperiment.State.Stop -> {}
            is OptimizationExperiment.State.Run -> {}
            is OptimizationExperiment.State.WaitDecision -> {
                state.processWaitDecisionState()
            }
        }
    }

    private fun OptimizationExperiment.State.WaitDecision.processWaitDecisionState() {
        targetFunctionValue
        inputParams
        makeDecision()
    }
}