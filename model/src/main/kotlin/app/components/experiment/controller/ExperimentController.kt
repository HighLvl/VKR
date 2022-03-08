package app.components.experiment.controller

import app.components.experiment.ExperimentTaskModel
import core.services.logger.Logger
import app.services.user.Service
import app.utils.splitOnCapitalChars
import kotlinx.coroutines.runBlocking
import core.services.logger.Level

class ExperimentController {
    private lateinit var taskModel: ExperimentTaskModel

    fun setTaskModel(taskModel: ExperimentTaskModel) {
        this.taskModel = taskModel
    }

    fun onModelUpdate(modelTime: Float) {
        stopModelOnTrueStopConditions(modelTime)
    }

    private fun stopModelOnTrueStopConditions(modelTime: Float) {
        val stopConditions =
            taskModel.stopConditions.associate { it.name.splitOnCapitalChars() to it.predicateFun() } +
                    ("Model Time More Than ${taskModel.stopTime}" to (modelTime > taskModel.stopTime)) +
                    ("Total Score More Than ${taskModel.stopScore}" to isTotalScoreMoreThan())


        stopConditions.entries.firstOrNull { it.value }?.let { (condName, _) ->
            Service.agentModelControl.stopModel()
            Logger.log("Stopped on $condName", Level.INFO)
        }
    }

    private fun isTotalScoreMoreThan() = taskModel.goals
        .asSequence()
        .filter { it.predicate.predicateFun() }
        .map { it.score }.sum() > taskModel.stopScore && (taskModel.constraints.all { it.predicateFun() })
}