package app.services.model.control.state

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import core.coroutines.launchWithAppContext
import app.services.model.control.ControlAction

object RunState : ConnectState() {
    override suspend fun pause(context: AgentModelControlContext) {
        context.periodTaskExecutor.pause()
        withContext(Dispatchers.IO) { context.apiClient.pause() }
        context.onPause()
        context.setState(PauseState)
    }

    override suspend fun stop(context: AgentModelControlContext) {
        context.periodTaskExecutor.stop()
        context.apiClient.stop()
        context.onStop()
        context.setState(StopState)
    }
}