package user.optimization

import core.components.base.AddInSnapshot
import core.components.base.Component
import core.components.base.Script
import core.components.experiment.OptimizationExperiment
import core.entities.getComponent
import core.services.Services
import imgui.ImGui
import org.lwjgl.glfw.GLFW

class UserDecisionMaking : Component, Script {
    private lateinit var optimizationExperiment: OptimizationExperiment

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
        optimizationExperiment = Services.scene.experimenter.getComponent()!!
    }

    override fun onModelStop() {
        needMakeDecision = false
    }

    override fun onModelAfterUpdate() {
        when (val state = optimizationExperiment.state) {
            is OptimizationExperiment.State.Stop, is OptimizationExperiment.State.Run -> needMakeDecision = false
            is OptimizationExperiment.State.WaitDecision -> if (!needMakeDecision){
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
        if (needMakeDecision && ImGui.isKeyPressed(GLFW.GLFW_KEY_RIGHT_CONTROL)) {
            waitDecision.makeDecision()
            needMakeDecision = false
            Services.agentModelControl.resumeModel()
        }
    }
}