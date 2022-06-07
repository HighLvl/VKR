package components.optimization.experiment

import core.components.experiment.ExperimentTaskModel
import core.components.experiment.MutableExperimentTaskModel
import core.components.experiment.OptimizationExperiment.Command
import core.coroutines.Contexts
import core.services.Services
import core.services.control.ControlState
import core.services.logger.Level
import core.services.logger.Logger
import core.utils.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

internal class OptimizationExperimentModel {
    lateinit var inputParams: MutableMap<String, InputDoubleParam>
    private val _isValidArgsObservable = PublishSubject<Boolean>()
    val isValidArgsObservable: Observable<Boolean> = _isValidArgsObservable

    private val _commandObservable = MutableValue<Command>(Command.Stop(false, Any(), 0.0))
    val commandObservable: ValueObservable<Command> = _commandObservable

    enum class State {
        STOP, RUN, WAIT_DECISION, WAIT_FIRST_UPDATE
    }

    var state: State = State.STOP
        private set

    var taskModel: ExperimentTaskModel = MutableExperimentTaskModel()
        set(value) {
            field = value
        }

    private val coroutineScope = CoroutineScope(Contexts.app)

    private val _ctrlMakeDecisionDataFlow = MutableSharedFlow<CtrlMakeDecisionData>()
    val ctrlMakeDecisionDataFlow: SharedFlow<CtrlMakeDecisionData> = _ctrlMakeDecisionDataFlow

    private var bestDecision = mapOf<String, Double>()
    private var bestTargetFunctionValue = Double.NEGATIVE_INFINITY
    private var lastDecision = mapOf<String, Double>()

    fun start() {
        bestDecision = mapOf()
        bestTargetFunctionValue = Double.NEGATIVE_INFINITY
        lastDecision = mapOf()

        if (state != State.STOP) return
        when (Services.agentModelControl.controlState) {
            ControlState.STOP -> Services.agentModelControl.runModel()
            ControlState.PAUSE -> Services.agentModelControl.resumeModel()
            else -> {
            }
        }
        startOptimization()
    }

    fun stop() {
        if (state == State.STOP) return
        stopOptimization(false)
    }

    private fun stopOptimization(hasGoalBeenAchieved: Boolean) = runBlocking {
        Services.agentModelControl.pauseModel()
        taskModel.stopOptimizationObservable.publish(Triple(hasGoalBeenAchieved, bestDecision, bestTargetFunctionValue))
        Logger.log(getString("opt_exp_stopped"), Level.INFO)
        state = State.STOP
        _commandObservable.publish(Command.Stop(hasGoalBeenAchieved, bestDecision, bestTargetFunctionValue))
    }

    suspend fun onModelUpdate() {
        when (state) {
            State.WAIT_FIRST_UPDATE -> waitInitialDecision()
            State.RUN -> processRunState()
            State.WAIT_DECISION -> processWaitDecisionState()
            else -> {
            }
        }
    }

    private suspend fun waitInitialDecision() {
        taskModel.updateObservable.publish()
        Logger.log(getString("make_initial_decision"), Level.INFO)
        state = State.WAIT_DECISION
        _commandObservable.publish(Command.MakeInitialDecision)
    }

    private fun startOptimization() = runBlocking {
        taskModel.startOptimizationObservable.publish()
        Logger.log(getString("opt_exp_started"), Level.INFO)
        _commandObservable.publish(Command.Start(inputParams.values.toList()))
        state = State.WAIT_FIRST_UPDATE
    }

    private suspend fun processRunState() {
        taskModel.updateObservable.publish()
        if (needStopOptimization()) {
            stopOptimization(false)
            return
        }

        if (needMakeDecision()) {
            taskModel.endObservable.publish()
            evaluateValue(taskModel.targetFunctionVH.value)
            val makeDecisionData = newMakeDecisionData()
            coroutineScope.launch { _ctrlMakeDecisionDataFlow.emit(makeDecisionData) }
            if (makeDecisionData.isTargetScoreAchieved) {
                Logger.log(getString("target_score_achieved"), Level.INFO)
                stopOptimization(true)
                return
            }
            _commandObservable.publish(Command.MakeDecision)
        }
    }

    private suspend fun evaluateValue(targetFunctionValue: Double) {
        if (targetFunctionValue > bestTargetFunctionValue) {
            bestTargetFunctionValue = targetFunctionValue
            bestDecision = lastDecision
        }
        state = State.WAIT_DECISION
        _commandObservable.publish(Command.EvaluateValue(targetFunctionValue))
    }

    private fun needMakeDecision(): Boolean {
        taskModel.makeDecisionConditions.entries.firstOrNull { it.value() }?.let { (condName, _) ->
            Logger.log(getString("make_decision_on", condName), Level.INFO)
            return true
        }
        return false
    }

    private fun needStopOptimization(): Boolean {
        var result = false
        taskModel.stopConditions.entries.firstOrNull { it.value() }?.let { (condName, _) ->
            Logger.log(getString("stop_on", condName), Level.INFO)
            result = true
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

    private fun newMakeDecisionData(): CtrlMakeDecisionData {
        val totalScore = taskModel.goals.sumOf { if (it.valueHolder.value) it.score else 0 }
        val isTargetScoreAchieved = totalScore >= taskModel.targetScore
        val goalValues = taskModel.goals.asSequence().map {
            val score = if (it.valueHolder.value) it.score else 0
            it.name to (it.valueHolder.value to score)
        }.toMap()
        return CtrlMakeDecisionData(
            taskModel.targetFunctionVH.value,
            goalValues,
            totalScore,
            isTargetScoreAchieved
        )
    }

    fun makeDecision(): Boolean = runBlocking {
        if (!commitInput()) return@runBlocking false
        taskModel.beginObservable.publish()
        state = State.RUN
        _commandObservable.publish(Command.Run)
        return@runBlocking true
    }

    fun isValidDecision(values: List<Double>): Boolean {
        val map = values.mapIndexed { index, value -> inputParams.values.toList()[index].name to value }.toMap()
        return taskModel.constraint(map)
    }

    private suspend fun commitInput(): Boolean {
        val params = inputParams.values.asSequence().map { it.name to it.value }.toMap()
        if (!taskModel.constraint(params)) return false
        taskModel.makeDecision(params)
        _isValidArgsObservable.publish(true)
        lastDecision = inputParams.asSequence().map { it.key to it.value.value }.toMap()
        return true
    }
}

data class CtrlMakeDecisionData(
    val targetFunctionValue: Double,
    val goalValues: Map<String, Pair<Boolean, Int>>,
    val totalScore: Int,
    val isTargetScoreAchieved: Boolean
)