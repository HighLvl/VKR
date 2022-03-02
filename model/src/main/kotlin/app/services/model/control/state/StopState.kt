package app.services.model.control.state

import app.logger.Log
import app.logger.Logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import core.coroutines.AppContext
import core.services.BehaviourRequestsReady
import core.services.EventBus
import core.services.listen
import kotlinx.coroutines.runBlocking

object StopState : ConnectState() {
    override suspend fun run(context: AgentModelControlContext, periodSec: Float) {
        withContext(Dispatchers.IO) {
            context.apiClient.run(context.globalArgs)
        }
        context.subscribeOnBehaviourRequestsReady()
        context.periodTaskExecutor.scheduleTask {
            withContext(AppContext.context) { updateAgentModel(context) }
        }
        context.onStart()
        context.setState(RunState)
    }

    private fun AgentModelControlContext.subscribeOnBehaviourRequestsReady() {
        behaviourRequestsReadyDisposable = EventBus.listen<BehaviourRequestsReady>().subscribe {
            runBlocking {
                withContext(Dispatchers.IO) {
                    apiClient.callBehaviourFunctions(it.behaviour)
                }
            }
        }
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