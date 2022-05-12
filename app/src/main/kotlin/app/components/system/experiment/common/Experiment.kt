package app.components.system.experiment.common

import app.components.system.base.Native
import app.utils.KtsScriptEngine
import core.utils.getString
import core.components.base.AddToSnapshot
import core.components.base.Component
import core.components.base.TargetEntity
import core.components.experiment.Experiment
import core.components.experiment.ExperimentTaskModel
import core.components.experiment.MutableExperimentTaskModel
import core.components.experiment.TrackedData
import core.entities.Experimenter
import core.services.Services
import core.services.control.ControlState
import core.services.logger.Level
import core.services.logger.Logger
import core.utils.EmptyPublishSubject
import core.utils.MutableValue
import core.utils.Observable
import core.utils.ValueObservable
import kotlinx.coroutines.runBlocking

@TargetEntity(Experimenter::class)
class Experiment : Component(), Experiment, Native, TrackedData {
    private var taskModel = MutableExperimentTaskModel()
    private val _taskModelObservable = MutableValue<ExperimentTaskModel>(taskModel)
    override val taskModelObservable: ValueObservable<ExperimentTaskModel> = _taskModelObservable

    private val _clearTrackedDataObservable = EmptyPublishSubject()
    override val clearTrackedDataObservable: Observable<Unit> = _clearTrackedDataObservable

    @AddToSnapshot(1)
    var task: String = ""
        set(value) {
            field = tryLoadExperimentTaskModel(value, field)
        }


    @AddToSnapshot(5)
    var trackedDataSize = Int.MAX_VALUE
        set(value) {
            if (value < 1) {
                Logger.log(getString("trackedDataSize_sbmt0"), Level.ERROR)
                return
            }
            field = value
            runBlocking {
                _trackedDataSizeObservable.publish(value)
            }
        }

    @AddToSnapshot(8)
    var clearTrackedDataOnRun = true

    private val _trackedDataSizeObservable = MutableValue(trackedDataSize)
    override val trackedDataSizeObservable: ValueObservable<Int> = _trackedDataSizeObservable

    override suspend fun onModelRun() {
        if (clearTrackedDataOnRun) {
            _clearTrackedDataObservable.publish()
        }
        taskModel.modelRunObservable.publish()
    }

    private fun tryLoadExperimentTaskModel(path: String, oldPath: String): String {
        when (Services.agentModelControl.controlState) {
            ControlState.RUN, ControlState.PAUSE -> {
                Logger.log(getString("stop_model_blt"), Level.ERROR)
                return oldPath
            }
            else -> {
            }
        }
        if (path.isEmpty()) return oldPath
        return try {
            taskModel = KtsScriptEngine().eval(path)
            runBlocking {
                _taskModelObservable.publish(taskModel)
            }
            path
        } catch (e: ClassCastException) {
            Logger.log(getString("experiment_tm_is_exp", ExperimentTaskModel::class), Level.ERROR)
            ""
        } catch (e: Exception) {
            Logger.log(getString("bad_exp_task_file"), Level.ERROR)
            Logger.log(e.stackTraceToString(), Level.ERROR)
            oldPath
        }
    }


    override suspend fun onModelUpdate() {
        taskModel.modelUpdateObservable.publish()
    }

    override suspend fun onModelStop() {
        taskModel.modelStopObservable.publish()
    }
}