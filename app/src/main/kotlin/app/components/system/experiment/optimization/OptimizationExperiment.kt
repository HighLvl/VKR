package app.components.system.experiment.optimization

import app.components.system.experiment.common.Experiment
import app.components.system.experiment.common.controller.OptimizationExperimentController
import app.components.system.experiment.common.goals.GoalsController
import app.components.system.experiment.common.input.InputArgsController
import core.components.base.AddInSnapshot
import core.components.base.Script
import core.components.experiment.ExperimentTaskModel
import core.components.experiment.OptimizationExperiment
import core.entities.getComponent
import core.services.Services

class OptimizationExperiment : OptimizationExperiment, Script {
    private val _inputParams = mutableMapOf<String, InputDoubleParam>()
    private val optimizationExperimentController = OptimizationExperimentController()
    private val _makeDecisionDataFlow = optimizationExperimentController.ctrlMakeDecisionDataFlow
    private val goalsController = GoalsController(_makeDecisionDataFlow)
    private val inputArgsController = InputArgsController()
    private val experiment = Services.scene.experimenter.getComponent<Experiment>()!!
    private val taskModel: ExperimentTaskModel by experiment::taskModel

    override var state: OptimizationExperiment.State = OptimizationExperiment.State.Stop
        private set

    override fun start() {
        optimizationExperimentController.start()
    }

    override fun stop() {
        optimizationExperimentController.stop()
    }

    @AddInSnapshot(6)
    var showGoals by goalsController::enabled

    @AddInSnapshot(7)
    var showInputArgs by inputArgsController::enabled

    @AddInSnapshot(8)
    var startOnModelRun = true

    override fun onAttach() {
        experiment.importTaskModelObservers += ::importOptimizationTaskModel
        experiment.clearTrackedDataObservers += ::reset
        experiment.trackedDataSizeObservers += ::setTrackedDataSize
        importOptimizationTaskModel()
    }

    override fun onDetach() {
        experiment.importTaskModelObservers -= ::importOptimizationTaskModel
        experiment.clearTrackedDataObservers -= ::reset
        experiment.trackedDataSizeObservers -= ::setTrackedDataSize
        optimizationExperimentController.stop()
    }

    private fun importOptimizationTaskModel() {
        reset()
        optimizationExperimentController.taskModel = taskModel
    }

    private fun setTrackedDataSize() {
        val value = experiment.trackedDataSize
        goalsController.trackedDataSize = value
        inputArgsController.trackedDataSize = value
    }

    override fun onModelRun() {
        if (startOnModelRun) {
            optimizationExperimentController.start()
        }
    }

    private fun reset() {
        goalsController.reset(taskModel.goals, taskModel.targetScore)
        resetInputArgsController()
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
        updateState()
    }

    private fun updateState() {
        state = when (optimizationExperimentController.state) {
            OptimizationExperimentController.State.STOP -> {
                OptimizationExperiment.State.Stop
            }
            OptimizationExperimentController.State.RUN -> {
                OptimizationExperiment.State.Run
            }
            OptimizationExperimentController.State.WAIT_DECISION -> {
                waitDecision()
            }
        }
    }

    private fun waitDecision() = object : OptimizationExperiment.State.WaitDecision {
        override val inputParams: List<OptimizationExperiment.Input>
            get() = _inputParams.values.toList()
        override val targetFunctionValue: Double
            get() = optimizationExperimentController.makeDecisionData!!.targetFunctionValue

        override fun makeDecision(): Boolean {
            val isValidArgs = optimizationExperimentController.makeDecision(inputParams)
            if (isValidArgs) {
                inputArgsController.commitInputArgs()
            }
            return isValidArgs
        }
    }

    override fun updateUI() {
        goalsController.update()
        inputArgsController.update()
    }

    override fun onModelStop() {
        optimizationExperimentController.onModelStop()
    }
}

