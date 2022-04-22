package components.optimization.experiment

import components.optimization.experiment.goals.GoalsController
import components.optimization.experiment.input.InputArgsController
import core.utils.getString
import core.components.base.AddToSnapshot
import core.components.base.Component
import core.components.base.TargetEntity
import core.components.experiment.Experiment
import core.components.experiment.ExperimentTaskModel
import core.components.experiment.OptimizationExperiment
import core.components.experiment.TrackedData
import core.entities.Experimenter
import core.entities.getComponent
import core.services.Services
import core.services.control.ControlState
import core.utils.Disposable
import core.utils.ValueObservable

@TargetEntity(Experimenter::class, [Experiment::class])
class OptimizationExperiment : Component(), OptimizationExperiment {
    private val _inputParams = mutableMapOf<String, InputDoubleParam>()
    private val optimizationExperimentController = OptimizationExperimentController()
    private val _makeDecisionDataFlow = optimizationExperimentController.ctrlMakeDecisionDataFlow
    private val goalsController = GoalsController(_makeDecisionDataFlow)
    private val inputArgsController = InputArgsController()
    private val experiment = Services.scene.experimenter.getComponent<Experiment>()!!
    private val trackedData = Services.scene.experimenter.getComponent<TrackedData>()!!
    private val taskModel: ExperimentTaskModel by experiment.taskModelObservable::value

    override val commandObservable: ValueObservable<OptimizationExperiment.Command> = optimizationExperimentController.commandObservable

    @AddToSnapshot(1)
    val info: String
    get() = when(Services.agentModelControl.controlState) {
        ControlState.DISCONNECT -> INFO_DISCONNECT
        else -> INFO_CONNECT
    }

    @AddToSnapshot(6)
    var showGoals by goalsController::enabled

    @AddToSnapshot(7)
    var showInputArgs by inputArgsController::enabled

    private val disposables = mutableListOf<Disposable>()

    override fun onAttach() {
        disposables += experiment.taskModelObservable.observe { importOptimizationTaskModel() }
        disposables += trackedData.clearTrackedDataObservable.observe { reset() }
        disposables += trackedData.trackedDataSizeObservable.observe { setTrackedDataSize(it) }
        importOptimizationTaskModel()
        optimizationExperimentController.isValidArgsObservable.observe {
            if (it) inputArgsController.commitInputArgs()
        }
    }

    override fun onDetach() {
        disposables.forEach { it.dispose() }
        optimizationExperimentController.stop()
    }

    private fun importOptimizationTaskModel() {
        reset()
        optimizationExperimentController.taskModel = taskModel
    }

    private fun setTrackedDataSize(value: Int) {
        goalsController.trackedDataSize = value
        inputArgsController.trackedDataSize = value
    }

    private fun reset() {
        goalsController.reset(taskModel.goals, taskModel.targetScore)
        resetInputArgsController()
        optimizationExperimentController.inputParams = _inputParams
    }

    private fun resetInputArgsController() {
        _inputParams.clear()
        taskModel.inputParams.forEach {
            val inputParam = InputDoubleParam(it.name, it.minValue, it.maxValue, it.step) { newValue ->
                inputArgsController.onChangeInputParamValue(it.name, newValue)
            }
            _inputParams[it.name] = inputParam
        }
        inputArgsController.reset(_inputParams)
        taskModel.inputParams.forEach {
            _inputParams[it.name]!!.value = it.initialValue
        }
    }

    override suspend fun onModelUpdate() {
        optimizationExperimentController.onModelUpdate()
    }

    override fun updateUI() {
        goalsController.update()
        inputArgsController.update()
        optimizationExperimentController.update()
    }

    override suspend fun onModelStop() {
        optimizationExperimentController.onModelStop()
    }

    override fun start() {
        optimizationExperimentController.start()
    }

    override fun stop() {
        optimizationExperimentController.stop()
    }

    override fun makeDecision(): Boolean = optimizationExperimentController.makeDecision()

    private companion object {
        val INFO_DISCONNECT = getString("opt_exp_info_disc")
        const val INFO_CONNECT = ""
    }
}

