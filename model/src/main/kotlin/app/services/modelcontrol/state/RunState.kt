package model.modelcontrol.service.state

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import core.coroutines.launchWithAppContext
import model.modelcontrol.service.ControlAction

object RunState : State() {
    private val AVAILABLE_CONTROL_ACTIONS = listOf(ControlAction.PAUSE, ControlAction.STOP)

    override fun pause(context: AgentModelControlContext) {
        launchWithAppContext {
            pauseAgentModel(context)
        }
    }

    private suspend fun pauseAgentModel(context: AgentModelControlContext) {
        context.periodTaskExecutor.pause()
        withContext(Dispatchers.IO) { context.apiClient.pause() }
        context.onPause()
        context.setState(PauseState)
    }

    override fun stop(context: AgentModelControlContext) {
        launchWithAppContext { stopAgentModel(context) }
    }

    private suspend fun stopAgentModel(context: AgentModelControlContext) {
        context.periodTaskExecutor.stop()
        context.apiClient.stop()
        context.onStop()
        context.setState(StopState)
    }

    override fun onThisStateChanged(context: AgentModelControlContext) {
        context.availableControlActions.onNext(AVAILABLE_CONTROL_ACTIONS)
    }
}