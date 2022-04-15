package user.optimization

import app.components.experiment.Experiment
import app.components.experiment.controller.OptimizationExperimentController
import app.components.experiment.goals.GoalsController
import app.components.experiment.input.InputArgsController
import core.components.base.AddInSnapshot
import core.components.base.Script
import core.components.experiment.*
import core.components.experiment.OptimizationExperiment
import core.entities.getComponent
import core.services.Services
import core.services.logger.Level
import core.services.logger.Logger
import core.services.modelTime
import kotlin.properties.Delegates

class OptimizationExperiment : OptimizationExperiment, Script {
    private val _inputParams = mutableMapOf<String, DoubleParam>()
    private val optimizationExperimentController = OptimizationExperimentController()
    private val _makeDecisionDataFlow = optimizationExperimentController.ctrlMakeDecisionDataFlow
    private val goalsController = GoalsController(_makeDecisionDataFlow)
    private val inputArgsController = InputArgsController()
    private val experiment = Services.scene.experimenter.getComponent<Experiment>()!!
    private val taskModel: ExperimentTaskModel by experiment::taskModel

    @AddInSnapshot(1)
    var trackedDataSize: Int = Int.MAX_VALUE
        set(value) {
            if (value < 1) {
                Logger.log("trackedDataSize should be more than 0", Level.ERROR)
                return
            }
            field = value
            goalsController.trackedDataSize = value
            inputArgsController.trackedDataSize = value
        }

    override var state: OptimizationExperiment.State = OptimizationExperiment.State.Stop
        private set

    @AddInSnapshot(6)
    var showGoals by goalsController::enabled

    @AddInSnapshot(7)
    var showInputArgs by inputArgsController::enabled

    @AddInSnapshot(8)
    var startOnModelRun = true

    override fun onAttach() {
        experiment.importTaskModelObservers += ::importOptimizationTaskModel
        importOptimizationTaskModel()
        experiment.clearTrackedDataObservers += ::reset
    }

    override fun onDetach() {
        experiment.importTaskModelObservers -= ::importOptimizationTaskModel
        experiment.clearTrackedDataObservers -= ::reset
    }

    private fun importOptimizationTaskModel() {
        reset()
        optimizationExperimentController.taskModel = taskModel
        importInputParams()
    }

    override fun onModelRun() {
        if (startOnModelRun) {
            optimizationExperimentController.start()
        }
    }

    private fun reset() {
        goalsController.reset(taskModel.goals, taskModel.targetScore)
        inputArgsController.reset(_inputParams)
    }

    private fun importInputParams() {
        _inputParams.clear()
        taskModel.inputParams.forEach {
            val inputParam = InputDoubleParam(it) { newValue ->
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
        when (optimizationExperimentController.state) {
            OptimizationExperimentController.State.STOP -> {
                state = OptimizationExperiment.State.Stop
            }
            OptimizationExperimentController.State.RUN -> {
                state = OptimizationExperiment.State.Run
            }
            OptimizationExperimentController.State.WAIT_DECISION -> {
                state = object : OptimizationExperiment.State.WaitDecision {
                    override val inputParams: List<DoubleParam>
                        get() = _inputParams.values.toList()
                    override val targetFunctionValue: Double
                        get() = optimizationExperimentController.makeDecisionData!!.targetFunctionValue

                    override fun makeDecision() {
                        inputArgsController.commitInputArgs()
                        optimizationExperimentController.makeDecision()
                    }
                }
            }
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

private class InputDoubleParam(override val inputParam: InputParam, onValueChanged: (Double) -> Unit) :
    DoubleParam {
    override var value: Double by Delegates.observable(0.0) { _, _, newValue ->
        onValueChanged(newValue)
    }

}