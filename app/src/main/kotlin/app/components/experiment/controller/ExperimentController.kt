package app.components.experiment.controller

import app.components.experiment.ExperimentTaskModel
import core.services.logger.Logger
import core.services.Services
import core.utils.splitOnCapitalChars
import core.services.logger.Level

class ExperimentController {
    private lateinit var taskModel: ExperimentTaskModel

    fun setTaskModel(taskModel: ExperimentTaskModel) {
        this.taskModel = taskModel
    }

    fun onModelUpdate(modelTime: Double) {
        stopModelOnTrueStopConditions(modelTime)
    }

    private fun stopModelOnTrueStopConditions(modelTime: Double) {
        val stopConditions =
            taskModel.stopConditions.associate { it.name.splitOnCapitalChars() to it.predicateExp() } +
                    ("Model Time >= ${taskModel.stopTime}" to (modelTime >= taskModel.stopTime)) +
                    ("Total Score >= ${taskModel.targetScore} and all constraints are true" to isTotalScoreGreaterThanOrEqualTo())


        stopConditions.entries.firstOrNull { it.value }?.let { (condName, _) ->
            Services.agentModelControl.stopModel()
            Logger.log("Stopped on $condName", Level.INFO)
        }
    }

    private fun isTotalScoreGreaterThanOrEqualTo() = taskModel.goals
        .asSequence()
        .map { it.targetFunction() }
        .sum() >= taskModel.targetScore && (taskModel.constraints.all { it.predicateExp() })
}