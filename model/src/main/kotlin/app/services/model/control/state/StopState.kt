package app.services.model.control.state

import app.logger.Log
import app.logger.Logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import core.coroutines.AppContext

object StopState : ConnectState() {
    override suspend fun run(context: AgentModelControlContext, periodSec: Float) {
        withContext(Dispatchers.IO) {
            context.apiClient.run(context.globalArgs)
        }
        context.periodTaskExecutor.scheduleTask {
            withContext(AppContext.context) { updateAgentModel(context) }
        }
        context.onStart()
        context.setState(RunState)
    }

    private suspend fun updateAgentModel(context: AgentModelControlContext) {
        Logger.log("start model update", Log.Level.DEBUG)
        Logger.log(Thread.currentThread().id.toString(), Log.Level.DEBUG)
        val snapshot = withContext(Dispatchers.IO) {
            context.apiClient.requestSnapshot()
        }
        context.onUpdate(snapshot)
        Logger.log("finish model update", Log.Level.DEBUG)
    }

    fun restoreRun(context: AgentModelControlContext) {
        context.periodTaskExecutor.scheduleTask {
            withContext(AppContext.context) { updateAgentModel(context) }
        }
        context.onStart()
        context.setState(RunState)
    }

    fun restorePause(context: AgentModelControlContext) {
        context.periodTaskExecutor.pause()
        context.periodTaskExecutor.scheduleTask {
            withContext(AppContext.context) { updateAgentModel(context) }
        }
        context.onStart()
        context.onPause()
        context.setState(PauseState)
    }
}