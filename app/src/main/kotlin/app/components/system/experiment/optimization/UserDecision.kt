package app.components.system.experiment.optimization

import core.components.base.AddInSnapshot
import core.components.base.Component
import core.components.base.Script
import core.components.experiment.OptimizationExperiment
import core.entities.Experimenter
import core.entities.getComponent
import core.services.Services
import core.services.logger.Level
import core.services.logger.Logger
import imgui.ImGui
import org.lwjgl.glfw.GLFW

class UserDecision : Component, Script {
    private lateinit var optimizationExperiment: OptimizationExperiment
    private lateinit var experimenter: Experimenter

    @AddInSnapshot(2)
    val info: String
        get() {
            return if (needMakeDecision) {
                "Change input args and press Right CTRL to make a decision"
            } else {
                "Waiting for the need to make a decision"
            }
        }

    private var needMakeDecision: Boolean = false
    private lateinit var waitDecision: OptimizationExperiment.State.WaitDecision

    override fun onAttach() {
        try {
            experimenter = Services.scene.experimenter
            optimizationExperiment = experimenter.getComponent()!!
        } catch (e: NullPointerException) {
            Services.scene.findEntityByComponent(this)!!.removeComponent(this::class)
            Logger.log("Optimization Experiment required", Level.ERROR)
        }
    }

    override fun onModelStop() {
        needMakeDecision = false
    }

    override fun onModelAfterUpdate() {
        when (val state = optimizationExperiment.state) {
            is OptimizationExperiment.State.Stop, is OptimizationExperiment.State.Run -> needMakeDecision = false
            is OptimizationExperiment.State.WaitDecision -> if (!needMakeDecision) {
                Services.agentModelControl.pauseModel { result ->
                    result.onSuccess {
                        needMakeDecision = true
                        waitDecision = state
                    }
                }
            }
        }
    }

    override fun updateUI() {
        if (experimenter.getComponent<OptimizationExperiment>() == null) {
            Services.scene.findEntityByComponent(this)!!.removeComponent(this::class)
            return
        }
        if (needMakeDecision && ImGui.isKeyPressed(GLFW.GLFW_KEY_RIGHT_CONTROL)) {
            if (waitDecision.makeDecision()) {
                needMakeDecision = false
                Services.agentModelControl.resumeModel()
            } else {
                Logger.log("Input args are not valid", Level.ERROR)
            }
        }
    }
}