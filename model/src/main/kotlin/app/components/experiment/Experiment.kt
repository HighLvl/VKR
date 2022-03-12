package app.components.experiment

import app.components.experiment.constraints.ConstraintsController
import app.components.experiment.controller.ExperimentController
import app.components.experiment.goals.GoalsController
import app.components.experiment.variables.mutable.MutableVariablesController
import app.components.experiment.variables.observable.ObservableVariablesController
import app.utils.KtsScriptEngine
import core.components.AddInSnapshot
import core.components.Script
import core.components.experiment.*
import core.components.experiment.Experiment
import core.services.logger.Level
import core.services.logger.Logger

class Experiment : Experiment, Script {
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
    var showObservableVariables
        set(value) {
            observableVariablesController.enabled = value
        }
        get() = observableVariablesController.enabled

    @AddInSnapshot(4)
    var showMutableVariables
        set(value) {
            mutableVariablesController.enabled = value
        }
        get() = mutableVariablesController.enabled

    @AddInSnapshot(5)
    var showConstraints
        set(value) {
            constraintsController.enabled = value
        }
        get() = constraintsController.enabled

    @AddInSnapshot(6)
    var showGoals
        set(value) {
            goalsController.enabled = value
        }
        get() = goalsController.enabled

    @AddInSnapshot(7)
    var clearTrackedDataOnRun = true

    override val goals: Set<Goal>
        get() = taskModel.goals
    override val observableVars: Map<String, GetterExp>
        get() = taskModel.observableVariables
    override val mutableVars: Map<String, SetterExp>
        get() = _mutableVars
    override val constraints: Set<Predicate>
        get() = taskModel.constraints

    private val observableVariablesController = ObservableVariablesController()
    private val mutableVariablesController = MutableVariablesController()
    private val constraintsController = ConstraintsController()
    private val goalsController = GoalsController()
    private val experimentController = ExperimentController()
    private var isRunning = false
    private val _mutableVars = mutableMapOf<String, SetterExp>()
    private var taskModel: ExperimentTaskModel = MutableExperimentTaskModel()


    init {
        importTaskModel()
    }

    override fun onModelRun() {
        isRunning = true
        if (clearTrackedDataOnRun) importTaskModel()
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
        subscribeControllerOnMutableVarsChange()
        observableVariablesController.reset(taskModel.observableVariables)
        mutableVariablesController.reset(taskModel.mutableVariables)
        constraintsController.reset(taskModel.constraints)
        goalsController.reset(taskModel.goals, taskModel.targetScore)
        experimentController.setTaskModel(taskModel)
    }

    private fun subscribeControllerOnMutableVarsChange() {
        with(_mutableVars) {
            clear()
            taskModel.mutableVariables.keys.asSequence().map { varName ->
                varName to { value: Float -> mutableVariablesController.onChangeMutableVarValue(varName, value) }
            }.also { putAll(it) }
        }
    }

    override fun onModelUpdate(modelTime: Float) {
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
    }

    override fun onModelStop() {
        isRunning = false
    }
}