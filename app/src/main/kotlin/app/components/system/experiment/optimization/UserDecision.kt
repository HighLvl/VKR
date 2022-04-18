package app.components.system.experiment.optimization

import app.utils.getString
import core.components.base.*
import core.components.experiment.OptimizationExperiment
import core.entities.Experimenter
import core.entities.getComponent
import core.services.Services
import core.services.logger.Level
import core.services.logger.Logger
import imgui.ImGui
import org.lwjgl.glfw.GLFW

@TargetEntity(Experimenter::class, [OptimizationExperiment::class])
class UserDecision : Component, Script {
    private lateinit var optimizationExperiment: OptimizationExperiment
    private lateinit var experimenter: Experimenter

    @AddInSnapshot(2)
    val info: String
        get() {
            return if (needMakeDecision) {
                getString("user_des_info_need_md")
            } else {
                getString("user_des_info_not_need_md")
            }
        }

    private var needMakeDecision: Boolean = false
    private lateinit var waitDecision: OptimizationExperiment.Command.WaitDecision

    override fun onAttach() {
        experimenter = Services.scene.experimenter
        optimizationExperiment = experimenter.getComponent()!!
        optimizationExperiment.commandObservable.observe {
            processCommand(it)
        }
    }

    private fun processCommand(command: OptimizationExperiment.Command) {
        when (command) {
            is OptimizationExperiment.Command.WaitDecision -> if (!needMakeDecision) {
                needMakeDecision = true
                waitDecision = command
                Services.agentModelControl.pauseModel()
            }
            else -> needMakeDecision = false
        }
    }

    override fun onModelStop() {
        needMakeDecision = false
    }

    override fun onModelAfterUpdate() {

    }

    override fun updateUI() {
        if (needMakeDecision && ImGui.isKeyPressed(GLFW.GLFW_KEY_RIGHT_CONTROL)) {
            if (waitDecision.makeDecision()) {
                needMakeDecision = false
                Services.agentModelControl.resumeModel()
            } else {
                Logger.log(getString("invalid_input_args"), Level.ERROR)
            }
        }
    }
}