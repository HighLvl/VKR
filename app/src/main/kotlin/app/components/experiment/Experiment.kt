package app.components.experiment

import app.components.base.SystemComponent
import app.components.experiment.variables.mutable.MutableVariablesController
import app.components.experiment.variables.observable.ObservableVariablesController
import app.utils.KtsScriptEngine
import core.components.base.AddInSnapshot
import core.components.base.Script
import core.components.experiment.Experiment
import core.components.experiment.ExperimentTaskModel
import core.components.experiment.MutableExperimentTaskModel
import core.services.logger.Level
import core.services.logger.Logger
import core.services.modelTime

class Experiment : Experiment, SystemComponent, Script {
    private val observableVariablesController = ObservableVariablesController()
    private val mutableVariablesController = MutableVariablesController()

    private var isRunning = false
    override var taskModel: ExperimentTaskModel = MutableExperimentTaskModel()
        private set

    val importTaskModelObservers = mutableListOf<() -> Unit>()
    val clearTrackedDataObservers = mutableListOf<() -> Unit>()

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
        }

    @AddInSnapshot(3)
    var showObservableVariables by observableVariablesController::enabled

    @AddInSnapshot(4)
    var showMutableVariables by mutableVariablesController::enabled

    @AddInSnapshot(8)
    var clearTrackedDataOnRun = true

    init {
        importTaskModel()
    }

    override fun onModelRun() {
        isRunning = true
        if (clearTrackedDataOnRun) {
            reset()
            clearTrackedDataObservers.forEach { it() }
        }
        taskModel.onModelRunListener()
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
        importTaskModelObservers.forEach { it() }
        reset()
    }

    private fun reset() {
        observableVariablesController.reset(taskModel.observableVariables)
        mutableVariablesController.reset(taskModel.mutableVariables)
    }

    override fun onModelUpdate() {
        taskModel.onModelUpdateListener()
        mutableVariablesController.onModelUpdate(modelTime)
    }

    override fun onModelAfterUpdate() {
        observableVariablesController.onModelUpdate(modelTime)
    }

    override fun updateUI() {
        observableVariablesController.update()
        mutableVariablesController.update()

    }

    override fun onModelStop() {
        isRunning = false
        taskModel.onModelStopListener()
    }
}