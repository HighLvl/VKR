package app.components.system.experiment.optimization

import app.utils.getString
import core.services.Services
import core.services.control.ControlState
import imgui.internal.ImGui

class OptimizationExperimentView(private val model: OptimizationExperimentModel) {
    private var notInitialized = true
    fun update() {
        if (Services.agentModelControl.controlState == ControlState.DISCONNECT) return

        if (notInitialized) {
            ImGui.setNextWindowPos(ImGui.getWindowWidth() / 2, ImGui.getWindowHeight() / 2)
            ImGui.setNextWindowSize(WINDOW_WIDTH, WINDOW_HEIGHT)
            notInitialized = false
        }
        if (ImGui.begin(WINDOW_TITLE)) {
            when (model.state) {
                OptimizationExperimentModel.State.RUN, OptimizationExperimentModel.State.WAIT_DECISION -> {
                    if (ImGui.button(STOP_BUTTON_TITLE)) {
                        model.stop()
                    }
                }
                else -> {
                    if (ImGui.button(START_BUTTON_TITLE)) {
                        model.start()
                    }
                }
            }
        }
        ImGui.end()
    }

    private companion object {
        val WINDOW_TITLE = getString("opt_exp_win_title")
        val START_BUTTON_TITLE = getString("start_opt_exp_button")
        val STOP_BUTTON_TITLE = getString("stop_opt_exp_button")
        const val WINDOW_WIDTH = 400f
        const val WINDOW_HEIGHT = 100f
    }
}
