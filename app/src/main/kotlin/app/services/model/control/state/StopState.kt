package app.services.model.control.state

import core.services.logger.Level
import core.services.logger.Logger

object StopState : ConnectState() {
    override fun run(context: AgentModelControlContext, periodSec: Float) {
        try {
            context.modelApi.run(context.globalArgs)
            context.periodTaskExecutor.scheduleTask {
                updateAgentModel(context)
            }
            context.onStart()
            context.setState(RunState)
        } catch (e: Exception) {
            disconnect(context)
            Logger.log(e.message.orEmpty(), Level.ERROR)
        }
    }

    private fun updateAgentModel(context: AgentModelControlContext) {
        try {
            val behaviour = context.getBehaviour()
            if (behaviour.requests.isNotEmpty())
                context.modelApi.callBehaviourFunctions(behaviour)
            val snapshot = context.modelApi.requestSnapshot()
            context.onUpdate(snapshot)
        } catch (e: Exception) {
            disconnect(context)
            Logger.log(e.message.orEmpty(), Level.ERROR)
        }
    }

    fun restoreRun(context: AgentModelControlContext) {
        context.periodTaskExecutor.scheduleTask {
            updateAgentModel(context)
        }
        context.onStart()
        context.setState(RunState)
    }

    fun restorePause(context: AgentModelControlContext) {
        context.periodTaskExecutor.pause()
        context.periodTaskExecutor.scheduleTask {
            updateAgentModel(context)
        }
        context.onStart()
        context.onPause()
        context.setState(PauseState)
    }
}