package app.components.experiment

import app.components.base.SystemComponent
import app.components.experiment.constraints.ConstraintsController
import app.components.experiment.controller.ExperimentController
import app.components.experiment.goals.GoalsController
import app.components.experiment.input.InputArgsController
import app.components.experiment.variables.mutable.MutableVariablesController
import app.components.experiment.variables.observable.ObservableVariablesController
import app.utils.KtsScriptEngine
import core.components.base.AddInSnapshot
import core.components.base.Script
import core.components.experiment.*
import core.components.experiment.Experiment
import core.services.logger.Level
import core.services.logger.Logger
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlin.properties.Delegates

class Experiment : Experiment, SystemComponent, Script {
    private val experimentController = ExperimentController()
    private val _modelRunResultFlow = experimentController.modelRunResultFlow
    private val observableVariablesController = ObservableVariablesController()
    private val mutableVariablesController = MutableVariablesController()
    private val constraintsController = ConstraintsController(_modelRunResultFlow)
    private val goalsController = GoalsController(_modelRunResultFlow)
    private val inputArgsController = InputArgsController()
    private var isRunning = false
    private var taskModel: ExperimentTaskModel = MutableExperimentTaskModel()
    private val _inputParams = mutableMapOf<String, DoubleParam>()

    @AddInSnapshot(1)
    var task: String = ""
        set(value) {
            field = tryLoadExperimentTaskModel(value, field)
        }

    @AddInSnapshot(2)
    var trackedDataSize = Int.MAX_VALUE
        set(value) {
            if (value < 1) {
                Logger.log("trackedDataSize should be more than 0", Level.ERROR)
                return
            }
            field = value
            observableVariablesController.trackedDataSize = value
            mutableVariablesController.trackedDataSize = value
            constraintsController.trackedDataSize = value
        }

    @AddInSnapshot(3)
    var showObservableVariables by observableVariablesController::enabled

    @AddInSnapshot(4)
    var showMutableVariables by mutableVariablesController::enabled

    @AddInSnapshot(5)
    var showConstraints by constraintsController::enabled

    @AddInSnapshot(6)
    var showGoals by goalsController::enabled

    @AddInSnapshot(7)
    var showInputArgs by inputArgsController::enabled

    @AddInSnapshot(8)
    var clearTrackedDataOnRun = true

    override val inputParams: List<DoubleParam>
        get() = _inputParams.values.toList()
    override val modelRunResultFlow: Flow<ModelRunResult>
        get() = _modelRunResultFlow.map {
            ModelRunResult(
                it.goalExpectedValues.values + it.constraintExpectedValues.values,
                it.isTargetScoreAchieved
            )
        }

    init {
        importTaskModel()
    }

    override fun onModelRun() {
        isRunning = true
        if (clearTrackedDataOnRun) reset()
        inputArgsController.onModelRun()
        experimentController.onModelRun()
    }

    private fun tryLoadExperimentTaskModel(path: String, oldPath: String): String {
        if (isRunning) {
            Logger.log("Stop model before loading task", Level.ERROR)
            return oldPath
        }
        if (path.isEmpty()) return oldPath
        return try {
            taskModel = KtsScriptEngine.eval(path)
            importTaskModel()
            path
        } catch (e: ClassCastException) {
            Logger.log("${ExperimentTaskModel::class} is expected", Level.ERROR)
            ""
        } catch (e: Exception) {
            Logger.log("Bad experiment task file", Level.ERROR)
            Logger.log(e.stackTraceToString(), Level.ERROR)
            oldPath
        }
    }

    private fun importTaskModel() {
        reset()
        experimentController.setTaskModel(taskModel)
        importInputParams()
    }

    private fun reset() {
        observableVariablesController.reset(taskModel.observableVariables)
        mutableVariablesController.reset(taskModel.mutableVariables)
        constraintsController.reset(taskModel.constraints)
        goalsController.reset(taskModel.goals, taskModel.targetScore)
        inputArgsController.reset(_inputParams)
    }

    private fun importInputParams() {
        _inputParams.clear()
        taskModel.inputParams.forEach {
            val inputParam = InputParam(it.setter) { newValue ->
                inputArgsController.onChangeInputParamValue(it.name, newValue)
            }
            _inputParams[it.name] = inputParam
        }
        inputArgsController.reset(_inputParams)
        taskModel.inputParams.forEach {
            _inputParams[it.name]!!.value = it.initialValue
        }
    }

    override fun onModelUpdate(modelTime: Double) {
        observableVariablesController.onModelUpdate(modelTime)
        mutableVariablesController.onModelUpdate(modelTime)
        constraintsController.onModelUpdate(modelTime)
        goalsController.onModelUpdate(modelTime)
        experimentController.onModelUpdate(modelTime)
    }

    override fun updateUI() {
        observableVariablesController.update()
        mutableVariablesController.update()
        constraintsController.update()
        goalsController.update()
        inputArgsController.update()
    }

    override fun onModelStop() {
        experimentController.onModelStop()
        isRunning = false
    }
}

private class InputParam(val setterExp: SetterExp, onValueChanged: (Double) -> Unit) :
    DoubleParam {
    override var value: Double by Delegates.observable(0.0) { _, _, newValue ->
        setterExp(newValue)
        onValueChanged(newValue)
    }
}