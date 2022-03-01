package app.components.experiment

import app.components.experiment.constraints.Constraints
import app.components.experiment.controller.ExperimentController
import app.components.experiment.goals.Goals
import app.components.experiment.variables.mutable.MutableVariables
import app.components.experiment.variables.observable.ObservableVariables
import app.logger.Log
import app.logger.Logger
import app.utils.KtsScriptEngine
import core.components.IgnoreInSnapshot
import core.components.Script
import core.components.SystemComponent
import kotlin.math.sin
import kotlin.system.measureTimeMillis

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
                Logger.log("trackedDataSize should be more them 0", Log.Level.ERROR)
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

    private val observableVariables = ObservableVariables()
    private val mutableVariables = MutableVariables()
    private val constraints = Constraints()
    private val goals = Goals()
    private val experimentController = ExperimentController()

    init {
        taskModel = experimentTask {
            val f = sequence<Int> {
                var a = 0
                while (true) {
                    a++
                    yield(a)
                }
            }.iterator()
            goal(10, "some Func") {
                f.next() < 100
            }
            constraint("d") {
                1 > 12
            }
            val v = sequence<Int> {
                var a = 0
                while (true) {
                    a++
                    yield(a)
                }
            }.iterator()
            constraint("f") {
                15 < v.next()
            }
            stopOn {
                condition { 2 + 5 > 10 }
                scoreMoreThan(6)
                timeMoreThan(9f)
            }
            val it = listOf(1f, 2f, 3f, 4f).iterator()
            val seq = sequence<Double> {
                var t = 0.0
                while (true) {
                    yield(sin(t))
                    t += 0.1
                }
            }.iterator()
            observableVariables(
                "x" to { if (it.hasNext()) it.next() else 0f },
                "y" to { seq.next().toFloat() }
            )
            mutableVariables("x" to {}, "y" to {})
        }
    }

    private fun tryLoadExperimentTaskModel(path: String): String {
        if (path.isEmpty()) return ""
        return try {
            taskModel = KtsScriptEngine.eval(path)
            importTaskModel()
            path
        } catch (e: Exception) {
            Logger.log("Bad experiment task file", Log.Level.ERROR)
            Logger.log(e.stackTraceToString(), Log.Level.ERROR)
            ""
        }
    }

    override fun onModelRun() {
        importTaskModel()
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

    override fun update() {
        observableVariables.update()
        mutableVariables.update()
        constraints.update()
        goals.update()
    }
}