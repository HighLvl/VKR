package components.optimization

import core.utils.getString
import core.components.base.AddInSnapshot
import core.components.base.Component
import core.components.base.Script
import core.components.base.TargetEntity
import core.entities.Experimenter
import core.entities.getComponent
import core.services.Services
import core.services.logger.Level
import core.services.logger.Logger
import core.utils.Disposable
import imgui.ImGui
import org.lwjgl.glfw.GLFW
import core.components.experiment.OptimizationExperiment

@TargetEntity(Experimenter::class, [OptimizationExperiment::class])
class UserDecision : Script() {
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
    private val disposables = mutableListOf<Disposable>()

    override fun onAttach() {
        experimenter = Services.scene.experimenter
        optimizationExperiment = experimenter.getComponent()!!
        disposables += optimizationExperiment.commandObservable.observe {
            processCommand(it)
        }
    }

    override fun onDetach() {
        disposables.forEach { it.dispose() }
    }

    private fun processCommand(command: OptimizationExperiment.Command) {
        when (command) {
            is OptimizationExperiment.Command.MakeDecision,
            OptimizationExperiment.Command.MakeInitialDecision -> if (!needMakeDecision) {
                needMakeDecision = true
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
            if (optimizationExperiment.makeDecision()) {
                needMakeDecision = false
                Services.agentModelControl.resumeModel()
            } else {
                Logger.log(getString("invalid_input_args"), Level.ERROR)
            }
        }
    }
}