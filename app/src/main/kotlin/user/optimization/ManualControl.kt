package user.optimization

import app.coroutines.Contexts
import core.components.base.AddInSnapshot
import core.components.base.Component
import core.components.base.Script
import core.components.experiment.OptimizationExperiment
import core.entities.getComponent
import core.services.Services
import imgui.ImGui
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.lwjgl.glfw.GLFW

class ManualControl : Component, Script {
    private val optimizationExperiment = Services.scene.experimenter.getComponent<OptimizationExperiment>()!!
    private val coroutineScope = CoroutineScope(Contexts.app)

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

    init {
        coroutineScope.launch {
            optimizationExperiment.makeDecisionConditionFlow.collect {
                needMakeDecision = true
            }
        }
        coroutineScope.launch {
            optimizationExperiment.stopOptimizationFlow.collect {
                needMakeDecision = false
            }
        }
    }

    override fun onModelStop() {
        needMakeDecision = false
    }

    override fun updateUI() {
        if (needMakeDecision && ImGui.isKeyPressed(GLFW.GLFW_KEY_RIGHT_CONTROL)) {
            optimizationExperiment.makeDecision()
            needMakeDecision = false
        }
    }
}