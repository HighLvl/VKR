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
        try {
            withContext(Dispatchers.IO) {
                context.apiClient.run(context.globalArgs)
            }
            context.subscribeOnBehaviourRequestsReady()
            context.periodTaskExecutor.scheduleTask {
                withContext(AppContext.context) { updateAgentModel(context) }
            }
            context.onStart()
            context.setState(RunState)
        } catch (e: Exception) {
            disconnect(context)
            Logger.log(e.message.orEmpty(), Log.Level.ERROR)
        }
    }

    private fun AgentModelControlContext.subscribeOnBehaviourRequestsReady() {
        behaviourRequestsReadyDisposable = EventBus.listen<BehaviourRequestsReady>().subscribe {
            runBlocking {
                try {
                    withContext(Dispatchers.IO) {
                        apiClient.callBehaviourFunctions(it.behaviour)
                    }
                } catch (e: Exception) {
                    disconnect(this@subscribeOnBehaviourRequestsReady)
                    Logger.log(e.message.orEmpty(), Log.Level.ERROR)
                }
            }
        }
    }

    private suspend fun updateAgentModel(context: AgentModelControlContext) {
        try {
            Logger.log("start model update", Log.Level.DEBUG)
            Logger.log(Thread.currentThread().id.toString(), Log.Level.DEBUG)
            val snapshot = withContext(Dispatchers.IO) {
                context.apiClient.requestSnapshot()
            }
            context.onUpdate(snapshot)
            Logger.log("finish model update", Log.Level.DEBUG)
        } catch (e: Exception) {
            disconnect(context)
            Logger.log(e.message.orEmpty(), Log.Level.ERROR)
        }
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