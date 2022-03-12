package app.components.experiment.goals

import app.components.experiment.ExperimentTaskModel
import core.components.experiment.Goal
import core.datatypes.base.MutableSeries
import core.datatypes.mutableSeriesOf

class GoalsController {
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
    private val goalsView = GoalsView(goalSeries)
    private val goalValues = mutableMapOf<String, Int>()
    private lateinit var goals: Map<String, Goal>
    private var targetScore: Int = 0

    fun reset(goals: Set<Goal>, targetScore: Int) {
        this.goals = goals.associateBy { it.predicate.name }
        this.targetScore = targetScore
        goalSeries.clear()
        goalValues.clear()
        goalSeries["t"] = mutableSeriesOf(trackedDataSize)
        this.goals.keys.forEach {
            goalSeries[it] = mutableSeriesOf(trackedDataSize)
        }
        goalSeries["Total Score"] = mutableSeriesOf(trackedDataSize)
        goalsView.reset()
    }

    fun onModelUpdate(modelTime: Float) {
        goals.values.forEach {
            goalValues[it.predicate.name] = when (it.predicate.predicateExp()) {
                true -> it.score
                else -> 0
            }
        }
        val prevGoalValues = goalSeries.entries.asSequence()
            .filterNot { it.key == "t" || it.key == "Total Score" }.map {
                it.key to it.value.last
            }.toMap()
        if (prevGoalValues != goalValues) {
            goalSeries.appendModelTime(modelTime)
            goalValues.forEach {
                goalSeries.appendGoalScore(it)
            }
            goalSeries.appendTotalScore()
        }
    }

    private fun Map<String, MutableSeries<Any>>.appendModelTime(modelTime: Float) {
        this["t"]!!.append(modelTime)
    }

    private fun Map<String, MutableSeries<Any>>.appendTotalScore() {
        this["Total Score"]!!.append(
            goalValues.values.sum() to targetScore
        )
    }

    private fun Map<String, MutableSeries<Any>>.appendGoalScore(it: Map.Entry<String, Int>) {
        this[it.key]!!.append(it.value)
    }

    fun update() {
        goalsView.update()
    }
}