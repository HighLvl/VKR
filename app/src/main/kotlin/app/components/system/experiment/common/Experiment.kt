package app.components.system.experiment.common

import app.components.system.base.Native
import app.components.system.experiment.common.variables.mutable.MutableVariablesController
import app.components.system.experiment.common.variables.observable.ObservableVariablesController
import app.utils.KtsScriptEngine
import core.components.base.AddInSnapshot
import core.components.base.Script
import core.components.base.TargetEntity
import core.components.experiment.Experiment
import core.components.experiment.ExperimentTaskModel
import core.components.experiment.MutableExperimentTaskModel
import core.entities.Experimenter
import core.services.Services
import core.services.control.ControlState
import core.services.logger.Level
import core.services.logger.Logger
import core.services.modelTime
import core.utils.EmptyPublishSubject
import core.utils.MutableValue
import core.utils.Observable
import core.utils.ValueObservable

@TargetEntity(Experimenter::class)
class Experiment : Experiment, Native, Script {
    private val observableVariablesController = ObservableVariablesController()
    private val mutableVariablesController = MutableVariablesController()

    private var taskModel = MutableExperimentTaskModel()
    private val _taskModelObservable = MutableValue<ExperimentTaskModel>(taskModel)
    override val taskModelObservable: ValueObservable<ExperimentTaskModel> = _taskModelObservable

    private val _clearTrackedDataObservable = EmptyPublishSubject()
    val clearTrackedDataObservable: Observable<Unit> = _clearTrackedDataObservable

    @AddInSnapshot(1)
    var task: String = ""
        set(value) {
            field = tryLoadExperimentTaskModel(value, field)
        }

    @AddInSnapshot(3)
    var showObservableVariables by observableVariablesController::enabled

    @AddInSnapshot(4)
    var showMutableVariables by mutableVariablesController::enabled

    @AddInSnapshot(5)
    var trackedDataSize = Int.MAX_VALUE
        set(value) {
            if (value < 1) {
                Logger.log("trackedDataSize should be more than 0", Level.ERROR)
                return
            }
            field = value
            observableVariablesController.trackedDataSize = value
            mutableVariablesController.trackedDataSize = value
            _trackedDataSizeObservable.value = value
        }

    @AddInSnapshot(8)
    var clearTrackedDataOnRun = true

    private val _trackedDataSizeObservable = MutableValue(trackedDataSize)
    val trackedDataSizeObservable: ValueObservable<Int> = _trackedDataSizeObservable

    override fun onAttach() {
        importTaskModel()
    }

    override fun onModelRun() {
        if (clearTrackedDataOnRun) {
            reset()
            _clearTrackedDataObservable.publish()
        }
        taskModel.modelRunObservable.publish()
    }

    private fun tryLoadExperimentTaskModel(path: String, oldPath: String): String {
        when (Services.agentModelControl.controlState) {
            ControlState.RUN, ControlState.PAUSE -> {
                Logger.log("Stop model before loading task", Level.ERROR)
                return oldPath
            }
            else -> { }
        }
        if (path.isEmpty()) return oldPath
        return try {
            taskModel = KtsScriptEngine.eval(path)
            importTaskModel()
            _taskModelObservable.value = taskModel
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
    }

    private fun reset() {
        observableVariablesController.reset(taskModel.observableVariables)
        mutableVariablesController.reset(taskModel.mutableVariables)
    }

    override fun onModelUpdate() {
        taskModel.modelUpdateObservable.publish()
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
        taskModel.modelStopObservable.publish()
    }
}