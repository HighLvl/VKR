package app.components.experiment

import app.logger.Log
import app.logger.Logger
import app.utils.KtsScriptEngine
import core.components.IgnoreInSnapshot
import core.components.Script
import core.components.SystemComponent
import core.datatypes.base.MutableSeries
import core.datatypes.mutableSeriesOf
import kotlin.math.sin

class Experiment : SystemComponent(), Script {
    var task: String = ""
        set(value) {
            field = tryLoadExperimentTaskModel(value)
        }

    @IgnoreInSnapshot
    var taskModel: ExperimentTaskModel = ExperimentTaskModel()
        private set
    var trackedDataSize = Int.MAX_VALUE
        set(value) {
            if (value < 1) {
                Logger.log("trackedDataSize should be more them 0", Log.Level.ERROR)
                return
            }
            field = value
            observableVariables.entries.forEach { (_, series) -> series.capacity = value }
        }
    private val observableVariables = mutableMapOf<String, MutableSeries<Float>>()
    var showObservableVariables
        set(value) {
            observableVariablesView.enabled = value
        }
        get() = observableVariablesView.enabled
    private val observableVariablesView = ObservableVariablesView(observableVariables)


    private val mutableVariables = mutableMapOf<String, MutableSeries<Float>>()
    var showMutableVariables
        set(value) {
            mutableVariablesView.enabled = value
        }
        get() = mutableVariablesView.enabled
    private val mutableVariablesView = MutableVariablesView(mutableVariables)
        .apply { onChangeValueListener = ::onChangeMutableVarValue }

    private val mutableVarValues = mutableMapOf<String, Float>()

    init {
        taskModel = experimentTask {
            targetFunc(10, "some Func") {
                4 < 100
            }
            constraint {
                1 > 12
            }
            constraint {
                3 > 15
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
            ""
        }
    }

    override fun start() {
        importTaskModel()
    }

    private fun importTaskModel() {
        observableVariables.clear()
        observableVariables["t"] = mutableSeriesOf(trackedDataSize)
        taskModel.observableVariables.entries.forEach { (varName, _) ->
            observableVariables[varName] = mutableSeriesOf(trackedDataSize)
        }
        observableVariablesView.reset()

        mutableVariables.clear()
        mutableVariables["t"] = mutableSeriesOf(trackedDataSize)
        taskModel.mutableVariables.entries.forEach { (varName, _) ->
            mutableVariables[varName] = mutableSeriesOf(trackedDataSize)
        }
        mutableVariablesView.reset()

        mutableVarValues.clear()
    }

    override fun onModelUpdate(modelTime: Float) {
        taskModel.observableVariables.forEach { (varName, getValue) ->
            observableVariables[varName]!!.append(getValue())
        }
        observableVariables["t"]!!.append(modelTime)

        commitMutableVarChanges(modelTime)
    }

    private fun commitMutableVarChanges(modelTime: Float) {
        if (mutableVarValues.isEmpty()) return
        mutableVariables["t"]!!.append(modelTime)
        mutableVariables.asSequence().filterNot { it.key == "t" }.forEach { (varName, series) ->
            val mutableVarValue = mutableVarValues[varName]
            if (mutableVarValue != null) {
                taskModel.mutableVariables[varName]!!.invoke(mutableVarValue)
            }
            series.append(mutableVarValue)
        }
        mutableVarValues.clear()
    }

    override fun update() {
        observableVariablesView.update()
        mutableVariablesView.update()
    }

    private fun onChangeMutableVarValue(varName: String, value: Float) {
        Logger.log("Set $varName to $value", Log.Level.DEBUG)
        mutableVarValues[varName] = value
    }
}
