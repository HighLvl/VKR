package app.components.experiment.goals

import app.components.experiment.controller.CtrlMakeDecisionData
import app.coroutines.Contexts
import core.components.experiment.Goal
import core.datatypes.base.MutableSeries
import core.datatypes.mutableSeriesOf
import core.services.modelTime
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

class GoalsController(makeDecisionDataFlow: SharedFlow<CtrlMakeDecisionData>) {
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
    private val goalValues = mutableMapOf<String, Pair<Boolean, Int>>()
    private lateinit var goals: Map<String, Goal>
    private var targetScore: Int = 0

    private val coroutineScope = CoroutineScope(Contexts.app)

    init {
        coroutineScope.launch {
            makeDecisionDataFlow.collect {
                appendFinalValues(it)
            }
        }
    }

    private fun appendFinalValues(makeDecisionData: CtrlMakeDecisionData) {
        goalSeries["t"]!!.append(modelTime)
        makeDecisionData.goalValues.forEach { entry ->
            goalSeries.appendGoalScore(entry.key to entry.value)
        }
        goalSeries[TITLE_TOTAL_SCORE]!!.append(makeDecisionData.totalScore)
        rowTypes.append(1)
    }

    fun reset(goals: Set<Goal>, targetScore: Int) {
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
            goalNameToScoreMap = goals.associate { it.name to it.score }
        }
    }

    private fun Map<String, MutableSeries<Any>>.appendGoalScore(it: Pair<String, Pair<Boolean, Int>>) {
        this[it.first]!!.append(it.second)
    }

    fun update() {
        goalsView.update()
    }

    private companion object {
        const val TITLE_TOTAL_SCORE = "Total Score"
    }
}