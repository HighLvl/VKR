package app.components.system.experiment.common.controller

import app.components.system.experiment.optimization.InputDoubleParam
import app.coroutines.Contexts
import app.utils.getString
import core.components.experiment.ExperimentTaskModel
import core.components.experiment.MutableExperimentTaskModel
import core.components.experiment.OptimizationExperiment.Command
import core.components.experiment.SetterExp
import core.services.Services
import core.services.control.ControlState
import core.services.logger.Level
import core.services.logger.Logger
import core.utils.MutableValue
import core.utils.Observable
import core.utils.PublishSubject
import core.utils.ValueObservable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

class OptimizationExperimentModel {
    lateinit var inputParams: MutableMap<String, InputDoubleParam>
    private val _isValidArgsObservable = PublishSubject<Boolean>()
    val isValidArgsObservable: Observable<Boolean> = _isValidArgsObservable

    private val _commandObservable = MutableValue<Command>(Command.Stop)
    val commandObservable: ValueObservable<Command> = _commandObservable

    enum class State {
        STOP, RUN, WAIT_DECISION
    }

    var state: State = State.STOP
        private set

    private var paramNameToSetterMap: Map<String, SetterExp> = mapOf()
    var taskModel: ExperimentTaskModel = MutableExperimentTaskModel()
        set(value) {
            field = value
            paramNameToSetterMap = value.inputParams.asSequence().map { it.name to it.setter }.toMap()
        }
    var makeDecisionData: CtrlMakeDecisionData? = null
        private set

    private val coroutineScope = CoroutineScope(Contexts.app)

    private val _ctrlMakeDecisionDataFlow = MutableSharedFlow<CtrlMakeDecisionData>()
    val ctrlMakeDecisionDataFlow: SharedFlow<CtrlMakeDecisionData> = _ctrlMakeDecisionDataFlow

    fun start() {
        if (state != State.STOP) return
        when (Services.agentModelControl.controlState) {
            ControlState.STOP -> Services.agentModelControl.runModel {
                it.onSuccess {
                    startOptimization()
                }
            }
            ControlState.PAUSE -> Services.agentModelControl.resumeModel {
                it.onSuccess {
                    startOptimization()
                }
            }
            ControlState.RUN -> startOptimization()
            else -> {
            }
        }
    }

    private fun startOptimization() {
        taskModel.startOptimizationObservable.publish()
        makeDecisionData = null
        Logger.log(getString("opt_exp_started"), Level.INFO)
        _commandObservable.value = Command.Start(inputParams.values.toList())
        state = State.RUN
        if (!makeDecision()) {
            Logger.log(getString("inv_init_val_of_params"), Level.ERROR)
            stop()
        }
    }

    fun stop() {
        if (state == State.STOP) return
        when (Services.agentModelControl.controlState) {
            ControlState.RUN -> Services.agentModelControl.pauseModel {
                it.onSuccess {
                    stopOptimization()
                }
            }
            else -> stopOptimization()
        }

    }

    private fun stopOptimization() {
        taskModel.stopOptimizationObservable.publish()
        Services.agentModelControl.pauseModel()
        Logger.log(getString("opt_exp_stopped"), Level.INFO)
        state = State.STOP
        _commandObservable.value = Command.Stop
    }

    fun onModelUpdate() {
        when (state) {
            State.RUN -> processRunState()
            State.WAIT_DECISION -> processWaitDecisionState()
            else -> {
            }
        }
    }

    private fun processRunState() {
        taskModel.updateObservable.publish()
        if (needStopOptimization()) {
            stop()
            return
        }

        if (needMakeDecision()) {
            taskModel.endObservable.publish()
            newMakeDecisionData()
            coroutineScope.launch { _ctrlMakeDecisionDataFlow.emit(makeDecisionData!!) }
            if (isTargetScoreAchieved()) {
                stop()
                return
            }
            waitDecision()
        }
    }

    private fun waitDecision() {
        state = State.WAIT_DECISION
        _commandObservable.value = waitDecisionState()
    }

    private fun waitDecisionState() = object : Command.WaitDecision {
        override val targetFunctionValue: Double
            get() = makeDecisionData!!.targetFunctionValue

        override fun makeDecision(): Boolean {
            return this@OptimizationExperimentModel.makeDecision()
        }
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

    private fun newMakeDecisionData() {
        val totalScore = taskModel.goals.sumOf { if (it.valueHolder.value) it.score else 0 }
        val isTargetScoreAchieved = totalScore >= taskModel.targetScore
        val goalValues = taskModel.goals.asSequence().map {
            val score = if (it.valueHolder.value) it.score else 0
            it.name to (it.valueHolder.value to score)
        }.toMap()
        makeDecisionData = CtrlMakeDecisionData(
            taskModel.targetFunctionVH.value,
            goalValues,
            totalScore,
            isTargetScoreAchieved
        )
        coroutineScope.launch { makeDecisionData }
    }

    private fun isTargetScoreAchieved(): Boolean {
        makeDecisionData?.isTargetScoreAchieved?.let {
            if (it) {
                Logger.log(getString("target_score_achieved"), Level.INFO)
            }
            return it
        }
        return false
    }

    private fun makeDecision(): Boolean {
        if (!commitInput()) return false
        taskModel.beginObservable.publish()
        state = State.RUN
        _commandObservable.value = Command.Run
        return true
    }

    private fun commitInput(): Boolean {
        val params = inputParams.values.asSequence().map { it.name to it.value }.toMap()
        if (!taskModel.constraint(params)) return false
        inputParams.values.forEach { paramNameToSetterMap[it.name]!!(it.value) }
        _isValidArgsObservable.publish(true)
        return true
    }
}

data class CtrlMakeDecisionData(
    val targetFunctionValue: Double,
    val goalValues: Map<String, Pair<Boolean, Int>>,
    val totalScore: Int,
    val isTargetScoreAchieved: Boolean
)