package app.components.experiment.goals

import app.components.experiment.controller.ModelRunResult
import app.coroutines.Contexts
import core.components.experiment.Goal
import core.datatypes.base.MutableSeries
import core.datatypes.mutableSeriesOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

class GoalsController(modelRunResultFlow: SharedFlow<ModelRunResult>) {
    var trackedDataSize = Int.MAX_VALUE
        set(value) {
            field = value
            goalSeries.entries.forEach { (_, series) -> series.capacity = value }
        }

    var enabled: Boolean
        set(value) {
            goalsView.enabled = value
        }
        get() = goalsView.enabled

    private val goalSeries = mutableMapOf<String, MutableSeries<Any>>()
    private val rowTypes = mutableSeriesOf<Int>()
    private val goalsView = GoalsView(goalSeries, rowTypes)
    private val goalValues = mutableMapOf<String, Double>()
    private lateinit var goals: Map<String, Goal>
    private var targetScore: Double = 0.0

    private val coroutineScope = CoroutineScope(Contexts.app)

    init {
        coroutineScope.launch {
            modelRunResultFlow.collect {
                appendExpectedValues(it)
            }
        }
    }

    private fun appendExpectedValues(modelRunResult: ModelRunResult) {
        goalSeries["t"]!!.append("EV")
        modelRunResult.goalExpectedValues.forEach { entry ->
            goalSeries.appendGoalScore(entry)
        }
        goalSeries[TITLE_TOTAL_SCORE]!!.append(modelRunResult.totalScore)
        rowTypes.append(1)
    }

    fun reset(goals: Set<Goal>, targetScore: Double) {
        this.goals = goals.associateBy { it.name }
        this.targetScore = targetScore
        goalValues.clear()

        with(goalSeries) {
            clear()
            put("t", mutableSeriesOf(trackedDataSize))
            this@GoalsController.goals.keys.forEach {
                put(it, mutableSeriesOf(trackedDataSize))
            }
            put(TITLE_TOTAL_SCORE, mutableSeriesOf(trackedDataSize))
        }
        rowTypes.clear()

        with(goalsView) {
            reset()
            this.targetScore = targetScore
            goalNameToRatingMap = goals.associate { it.name to it.rating }
        }
    }

    fun onModelUpdate(modelTime: Double) {
        goals.values.forEach {
            goalValues[it.name] = it.targetFunction()
        }
        val prevGoalValues = goalSeries.entries.asSequence()
            .filterNot { it.key == "t" || it.key == TITLE_TOTAL_SCORE }.map {
                it.key to it.value.last
            }.toMap()
        if (prevGoalValues != goalValues) {
            goalSeries.appendModelTime(modelTime)
            goalValues.forEach {
                goalSeries.appendGoalScore(it)
            }
            goalSeries.appendTotalScore()
            rowTypes.append(0)
        }
    }

    private fun Map<String, MutableSeries<Any>>.appendModelTime(modelTime: Double?) {
        this["t"]!!.append(modelTime)
    }

    private fun Map<String, MutableSeries<Any>>.appendTotalScore() {
        this[TITLE_TOTAL_SCORE]!!.append(
            goalValues.values.sum()
        )
    }

    private fun Map<String, MutableSeries<Any>>.appendGoalScore(it: Map.Entry<String, Double>) {
        this[it.key]!!.append(it.value)
    }

    fun update() {
        goalsView.update()
    }

    private companion object {
        const val TITLE_TOTAL_SCORE = "Total Score"
    }
}