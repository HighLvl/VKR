package app.services.model.control.state

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import core.coroutines.AppContext
import core.coroutines.launchWithAppContext
import app.services.model.control.ControlAction

object StopState : State() {
    private val AVAILABLE_CONTROL_ACTIONS = listOf(ControlAction.RUN)

    override fun run(context: AgentModelControlContext, periodSec: Float) {
        launchWithAppContext {
            runAgentModel(context, periodSec)
        }
    }

    private suspend fun runAgentModel(context: AgentModelControlContext, periodSec: Float) {
        withContext(Dispatchers.IO) {
            context.apiClient.run(context.globalArgs)
        }
        context.periodTaskExecutor.scheduleTask(periodSec) {
            withContext(AppContext.context) { updateAgentModel(context) }
        }
        context.onStart()
        context.setState(RunState)
    }

    private suspend fun updateAgentModel(context: AgentModelControlContext) {
        println("start model update")
        println(Thread.currentThread().id)
        val snapshot = withContext(Dispatchers.IO) {
            context.apiClient.requestSnapshot()
        }
        context.onUpdate(snapshot)
        println("finish model update")
    }

    override fun onThisStateChanged(context: AgentModelControlContext) {
        context.availableControlActions.onNext(AVAILABLE_CONTROL_ACTIONS)
    }
}