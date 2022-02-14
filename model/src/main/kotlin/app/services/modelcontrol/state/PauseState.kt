package model.modelcontrol.service.state

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import core.coroutines.launchWithAppContext
import model.modelcontrol.service.ControlAction

object PauseState : State() {
    private val AVAILABLE_CONTROL_ACTIONS = listOf(ControlAction.RESUME, ControlAction.STOP)

    override fun resume(context: AgentModelControlContext) {
        launchWithAppContext { resumeAgentModel(context) }
    }

    private suspend fun resumeAgentModel(context: AgentModelControlContext) {
        context.apiClient.resume()
        withContext(Dispatchers.IO) { context.periodTaskExecutor.resume() }
        context.onResume()
        context.setState(RunState)
    }

    override fun stop(context: AgentModelControlContext) {
        launchWithAppContext { stopAgentModel(context) }
    }

    private suspend fun stopAgentModel(context: AgentModelControlContext) {
        context.periodTaskExecutor.stop()
        withContext(Dispatchers.IO) { context.apiClient.stop() }
        context.onStop()
        context.setState(StopState)
    }

    override fun onThisStateChanged(context: AgentModelControlContext) {
        context.availableControlActions.onNext(AVAILABLE_CONTROL_ACTIONS)

    }
}