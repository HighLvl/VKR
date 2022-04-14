package app.components.experiment.controller

import core.components.experiment.ExperimentTaskModel
import app.coroutines.Contexts
import core.services.Services
import core.services.logger.Level
import core.services.logger.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

class OptimizationExperimentController {
    private lateinit var taskModel: ExperimentTaskModel

    private val _makeDecisionConditionFlow = MutableSharedFlow<MakeDecisionCondition>()
    private val coroutineScope = CoroutineScope(Contexts.app)
    private var needMakeDecision = false
    private val _stopOptimizationFlow = MutableSharedFlow<String>()
    private var stopConditionName: String? = null

    val makeDecisionConditionFlow: SharedFlow<MakeDecisionCondition> = _makeDecisionConditionFlow
    val stopOptimizationFlow: SharedFlow<String> = _stopOptimizationFlow

    fun setTaskModel(taskModel: ExperimentTaskModel) {
        this.taskModel = taskModel
    }

    fun onModelRun() {
        taskModel.onModelRunListener()
        taskModel.onBeginListeners.forEach { it() }
    }

    fun onModelUpdate(modelTime: Double) {
        taskModel.onModelUpdateListener()
        taskModel.onUpdateListeners.forEach { it() }
        makeDecisionOnTrueConditions()
        stopOptimizationOnTrueConditions()
    }
    
    private fun makeDecisionOnTrueConditions() {
        taskModel.makeDecisionConditions.entries.firstOrNull {it.value()}?.let {(condName, _) ->
            Logger.log("Make decision on \"$condName\"", Level.INFO)
            Services.agentModelControl.pauseModel()
            needMakeDecision = true
        }
    }

    private fun stopOptimizationOnTrueConditions() {
        taskModel.stopConditions.entries.firstOrNull { it.value() }?.let { (condName, _) ->
            Logger.log("Stop optimization on $condName", Level.INFO)
            Services.agentModelControl.pauseModel()
            stopConditionName = condName
        }
    }

    fun onModelStop() {
        taskModel.onModelStopListener()
        needMakeDecision = false
    }

    private fun buildMakeDecisionCondition(): MakeDecisionCondition {
        val totalScore = taskModel.goals.sumOf { (_, rating, vh) ->
            if (vh.value >= rating) rating else 0.0
        }
        val isTargetScoreAchieved =
            totalScore >= taskModel.targetScore && taskModel.constraints.all {it.valueHolder.value}
        val goalValues = taskModel.goals.asSequence().map { it.name to it.targetFunctionVH.value }.toMap()
        val constraintValues = taskModel.constraints.asSequence().map { it.name to it.valueHolder.value }.toMap()
        return MakeDecisionCondition(
            goalValues,
            constraintValues,
            totalScore,
            isTargetScoreAchieved
        )
    }

    fun onModelPause() {
        if (needMakeDecision) {
            taskModel.onEndListeners.forEach { it() }
            emitMakeDecisionCondition(buildMakeDecisionCondition())
        }
        stopConditionName?.let {
            coroutineScope.launch { _stopOptimizationFlow.emit(it) }
            stopConditionName = null
        }
    }

    private fun emitMakeDecisionCondition(result: MakeDecisionCondition) {
        coroutineScope.launch {
            _makeDecisionConditionFlow.emit(result)
        }
    }

    fun makeDecision() {
        Services.agentModelControl.resumeModel()
        needMakeDecision = false
        taskModel.onBeginListeners.forEach { it() }
    }
}

data class MakeDecisionCondition(
    val goalValues: Map<String, Double>,
    val constraintValues: Map<String, Boolean>,
    val totalScore: Double,
    val isTargetScoreAchieved: Boolean
)