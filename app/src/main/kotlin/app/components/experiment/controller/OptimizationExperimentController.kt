package app.components.experiment.controller

import app.coroutines.Contexts
import core.components.experiment.ExperimentTaskModel
import core.services.Services
import core.services.logger.Level
import core.services.logger.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

class OptimizationExperimentController  {

    enum class State {
        STOP, RUN, WAIT_DECISION
    }
    var state = State.STOP
    private set

    lateinit var taskModel: ExperimentTaskModel
    var makeDecisionData: CtrlMakeDecisionData? = null
    private set

    private val _ctrlMakeDecisionDataFlow = MutableSharedFlow<CtrlMakeDecisionData>()
    private val coroutineScope = CoroutineScope(Contexts.app)

    val ctrlMakeDecisionDataFlow: SharedFlow<CtrlMakeDecisionData> = _ctrlMakeDecisionDataFlow

    fun start() {
        taskModel.onStartOptimizationListeners.forEach { it() }
        taskModel.onBeginListeners.forEach { it() }
        state = State.RUN
        makeDecisionData = null
        Logger.log("Optimization experiment started", Level.INFO)
    }

    fun stop() {
        if (state == State.STOP) return
        taskModel.onStopOptimizationListeners.forEach { it() }
        state = State.STOP
        Services.agentModelControl.pauseModel()
        Logger.log("Optimization experiment stopped", Level.INFO)
    }

    fun onModelUpdate(modelTime: Double) {
        when(state) {
            State.RUN -> processRunState()
            State.WAIT_DECISION -> processWaitDecisionState()
            else -> {}
        }
    }

    private fun processRunState() {
        taskModel.onUpdateListeners.forEach { it() }
        if (needMakeDecision()) {
            taskModel.onEndListeners.forEach { it() }
            newMakeDecisionData()
            waitDecision()
            coroutineScope.launch { _ctrlMakeDecisionDataFlow.emit(makeDecisionData!!)}
        }
        if (needStopOptimization()) {
            stop()
        }
    }

    private fun waitDecision() {
        state = State.WAIT_DECISION
    }

    private fun needMakeDecision(): Boolean {
        taskModel.makeDecisionConditions.entries.firstOrNull {it.value()}?.let {(condName, _) ->
            Logger.log("Make decision on \"$condName\"", Level.INFO)
            return true
        }
        return false
    }

    private fun needStopOptimization(): Boolean {
        var result = false
        taskModel.stopConditions.entries.firstOrNull { it.value() }?.let { (condName, _) ->
            Logger.log("Stop on \"$condName\"", Level.INFO)
            result = true
        }
        makeDecisionData?.isTargetScoreAchieved?.let {
            if (it) {
                Logger.log("Target score achieved", Level.INFO)
                result = true
            }
        }
        return result
    }

    private fun processWaitDecisionState() {
        if (needStopOptimization()) {
            stop()
        }
    }

    fun onModelStop() {
        stop()
    }

    private fun newMakeDecisionData() {
        val totalScore = taskModel.goals.sumOf { if(it.valueHolder.value) it.score else 0 }
        val isTargetScoreAchieved = totalScore >= taskModel.targetScore
        val goalValues = taskModel.goals.asSequence().map {
            val score = if (it.valueHolder.value) it.score else 0
            it.name to (it.valueHolder.value to score)
        }.toMap()
        makeDecisionData =  CtrlMakeDecisionData(
            taskModel.targetFunctionVH.value,
            goalValues,
            totalScore,
            isTargetScoreAchieved
        )
        coroutineScope.launch { makeDecisionData }
    }

    fun makeDecision() {
        taskModel.onBeginListeners.forEach { it() }
        state = State.RUN
    }
}

data class CtrlMakeDecisionData(
    val targetFunctionValue: Double,
    val goalValues: Map<String, Pair<Boolean, Int>>,
    val totalScore: Int,
    val isTargetScoreAchieved: Boolean
)