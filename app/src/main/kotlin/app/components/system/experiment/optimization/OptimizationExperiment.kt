package app.components.system.experiment.optimization

import app.components.system.experiment.common.Experiment
import app.components.system.experiment.common.goals.GoalsController
import app.components.system.experiment.common.input.InputArgsController
import core.components.base.AddInSnapshot
import core.components.base.Script
import core.components.base.TargetEntity
import core.components.experiment.ExperimentTaskModel
import core.components.experiment.OptimizationExperiment
import core.entities.Experimenter
import core.entities.getComponent
import core.services.Services
import core.services.control.ControlState
import core.utils.Disposable
import core.utils.ValueObservable

@TargetEntity(Experimenter::class, [core.components.experiment.Experiment::class])
class OptimizationExperiment : OptimizationExperiment, Script {
    private val _inputParams = mutableMapOf<String, InputDoubleParam>()
    private val optimizationExperimentController = OptimizationExperimentController()
    private val _makeDecisionDataFlow = optimizationExperimentController.ctrlMakeDecisionDataFlow
    private val goalsController = GoalsController(_makeDecisionDataFlow)
    private val inputArgsController = InputArgsController()
    private val experiment = Services.scene.experimenter.getComponent<Experiment>()!!
    private val taskModel: ExperimentTaskModel by experiment.taskModelObservable::value

    override val commandObservable: ValueObservable<OptimizationExperiment.Command> = optimizationExperimentController.commandObservable

    @AddInSnapshot(1)
    val info: String
    get() = when(Services.agentModelControl.controlState) {
        ControlState.DISCONNECT -> INFO_DISCONNECT
        else -> INFO_CONNECT
    }

    @AddInSnapshot(6)
    var showGoals by goalsController::enabled

    @AddInSnapshot(7)
    var showInputArgs by inputArgsController::enabled

    private val disposables = mutableListOf<Disposable>()

    override fun onAttach() {
        disposables += experiment.taskModelObservable.observe { importOptimizationTaskModel() }
        disposables += experiment.clearTrackedDataObservable.observe { reset() }
        disposables += experiment.trackedDataSizeObservable.observe { setTrackedDataSize(it) }
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

    override fun onModelUpdate() {
        optimizationExperimentController.onModelUpdate()
    }

    override fun updateUI() {
        goalsController.update()
        inputArgsController.update()
        optimizationExperimentController.update()
    }

    override fun onModelStop() {
        optimizationExperimentController.onModelStop()
    }

    override fun start() {
        optimizationExperimentController.start()
    }

    override fun stop() {
        optimizationExperimentController.stop()
    }

    private companion object {
        const val INFO_DISCONNECT = "Connect to the model to be able to run an optimization experiment"
        const val INFO_CONNECT = ""
    }
}

