package app.components.experiment.controller

import core.components.experiment.ExperimentTaskModel
import app.coroutines.Contexts
import core.components.experiment.Goal
import core.components.experiment.Predicate
import core.services.Services
import core.services.logger.Level
import core.services.logger.Logger
import core.utils.splitOnCapitalChars
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

class ExperimentController {
    private lateinit var taskModel: ExperimentTaskModel
    private var prevTime = 0.0
    private val goalToExpectedValues = mutableMapOf<Goal, Double>()
    private val constraintToExpectedValues = mutableMapOf<Predicate, Double>()
    private val _modelRunResultFlow = MutableSharedFlow<ModelRunResult>()
    private val coroutineScope = CoroutineScope(Contexts.app)

    val modelRunResultFlow: SharedFlow<ModelRunResult> = _modelRunResultFlow

    fun setTaskModel(taskModel: ExperimentTaskModel) {
        this.taskModel = taskModel
    }

    fun onModelRun() {
        prevTime = 0.0
        with(goalToExpectedValues) {
            clear()
            taskModel.goals.forEach { put(it, 0.0) }
        }
        with(constraintToExpectedValues) {
            clear()
            taskModel.constraints.forEach { put(it, 0.0) }
        }
    }

    fun onModelUpdate(modelTime: Double) {
        val dt = modelTime - prevTime
        prevTime = modelTime
        updateGoalExpectedValues(dt)
        updateConstraintExpectedValues(dt)

        stopModelOnTrueStopConditions(modelTime)
    }

    private fun updateGoalExpectedValues(dt: Double) {
        goalToExpectedValues.forEach { (goal, value) ->
            val instValue = goal.targetFunction()
            goalToExpectedValues[goal] = value + dt * instValue
        }
    }

    private fun updateConstraintExpectedValues(dt: Double) {
        constraintToExpectedValues.forEach { (constraint, value) ->
            val instValue = if (constraint.predicateExp()) 1.0 else 0.0
            constraintToExpectedValues[constraint] = value + dt * instValue
        }
    }

    private fun stopModelOnTrueStopConditions(modelTime: Double) {
        val stopConditions =
            taskModel.stopConditions.associate { it.name.splitOnCapitalChars() to it.predicateExp() } +
                    ("Model Time >= ${taskModel.stopTime}" to (modelTime >= taskModel.stopTime))

        stopConditions.entries.firstOrNull { it.value }?.let { (condName, _) ->
            Services.agentModelControl.stopModel()
            Logger.log("Stopped on $condName", Level.INFO)
        }
    }

    fun onModelStop() {
        if (prevTime == 0.0) return
        calculateGoalExpectedValues()
        calculateConstraintExpectedValues()
        val result = buildModelRunResult()
        emitModelRunResult(result)
    }

    private fun calculateGoalExpectedValues() {
        goalToExpectedValues.forEach { (goal, value) ->
            goalToExpectedValues[goal] = value / prevTime
        }
    }

    private fun calculateConstraintExpectedValues() {
        constraintToExpectedValues.forEach { (constraint, value) ->
            constraintToExpectedValues[constraint] = value / prevTime
        }
    }

    private fun buildModelRunResult(): ModelRunResult {
        val totalScore = goalToExpectedValues.map { (goal, expectedValue) ->
            if (expectedValue >= goal.rating) goal.rating else 0.0
        }.sum()
        val isTargetScoreAchieved =
            totalScore >= taskModel.targetScore && constraintToExpectedValues.values.all { it == 1.0 }
        val goalExpectedValues = goalToExpectedValues.map { it.key.name to it.value }.toMap()
        val constraintExpectedValues = constraintToExpectedValues.map { it.key.name to it.value }.toMap()
        return ModelRunResult(
            goalExpectedValues,
            constraintExpectedValues,
            totalScore,
            isTargetScoreAchieved
        )
    }

    private fun emitModelRunResult(result: ModelRunResult) {
        coroutineScope.launch {
            _modelRunResultFlow.emit(result)
        }
    }
}

data class ModelRunResult(
    val goalExpectedValues: Map<String, Double>,
    val constraintExpectedValues: Map<String, Double>,
    val totalScore: Double,
    val isTargetScoreAchieved: Boolean
)