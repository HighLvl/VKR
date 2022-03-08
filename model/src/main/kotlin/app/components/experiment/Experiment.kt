package app.components.experiment

import app.components.experiment.constraints.Constraints
import app.components.experiment.controller.ExperimentController
import app.components.experiment.goals.Goals
import app.components.experiment.variables.mutable.MutableVariables
import app.components.experiment.variables.observable.ObservableVariables
import core.services.logger.Logger
import app.utils.KtsScriptEngine
import core.components.IgnoreInSnapshot
import core.components.Script
import core.components.SystemComponent
import core.services.logger.Level
import core.services.logger.Log
import java.lang.ClassCastException
import kotlin.math.sin

class Experiment : SystemComponent(), Script {
    var task: String = ""
        set(value) {
            field = tryLoadExperimentTaskModel(value)
        }
    @IgnoreInSnapshot
    var taskModel: ExperimentTaskModel = MutableExperimentTaskModel()
        private set
    var trackedDataSize = Int.MAX_VALUE
        set(value) {
            if (value < 1) {
                Logger.log("trackedDataSize should be more than 0", Level.ERROR)
                return
            }
            field = value
            observableVariables.trackedDataSize = value
            mutableVariables.trackedDataSize = value
            constraints.trackedDataSize = value
        }
    var showObservableVariables
        set(value) {
            observableVariables.enabled = value
        }
        get() = observableVariables.enabled
    var showMutableVariables
        set(value) {
            mutableVariables.enabled = value
        }
        get() = mutableVariables.enabled
    var showConstraints
        set(value) {
            constraints.enabled = value
        }
        get() = constraints.enabled
    var showGoals
        set(value) {
            goals.enabled = value
        }
        get() = goals.enabled
    var clearTrackedDataOnRun = true

    private val observableVariables = ObservableVariables()
    private val mutableVariables = MutableVariables()
    private val constraints = Constraints()
    private val goals = Goals()
    private val experimentController = ExperimentController()

    init {
        importTaskModel()
    }

    override fun onModelRun() {
        if (clearTrackedDataOnRun) importTaskModel()
    }

    private fun tryLoadExperimentTaskModel(path: String): String {
        if (path.isEmpty()) return ""
        return try {
            taskModel = KtsScriptEngine.eval(path)
            importTaskModel()
            path
        }
        catch (e: ClassCastException) {
            Logger.log("${ExperimentTaskModel::class} is expected", Level.ERROR)
            ""
        }
        catch (e: Exception) {
            Logger.log("Bad experiment task file", Level.ERROR)
            Logger.log(e.stackTraceToString(), Level.ERROR)
            ""
        }
    }

    private fun importTaskModel() {
        observableVariables.reset(taskModel.observableVariables)
        mutableVariables.reset(taskModel.mutableVariables)
        constraints.reset(taskModel.constraints)
        goals.reset(taskModel.goals, taskModel.stopScore)
        experimentController.setTaskModel(taskModel)
    }

    override fun onModelUpdate(modelTime: Float) {
        observableVariables.onModelUpdate(modelTime)
        mutableVariables.onModelUpdate(modelTime)
        constraints.onModelUpdate(modelTime)
        goals.onModelUpdate(modelTime)
        experimentController.onModelUpdate(modelTime)
    }

    override fun updateUI() {
        observableVariables.update()
        mutableVariables.update()
        constraints.update()
        goals.update()
    }
}