package app.services.model.control.state

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import core.coroutines.launchWithAppContext
import app.services.model.control.ControlAction

object PauseState : ConnectState() {
    override suspend fun resume(context: AgentModelControlContext) {
        resumeAgentModel(context)
    }

    private suspend fun resumeAgentModel(context: AgentModelControlContext) {
        context.apiClient.resume()
        withContext(Dispatchers.IO) { context.periodTaskExecutor.resume() }
        context.onResume()
        context.setState(RunState)
    }

    override suspend fun stop(context: AgentModelControlContext) {
        context.periodTaskExecutor.stop()
        withContext(Dispatchers.IO) { context.apiClient.stop() }
        context.onStop()
        context.setState(StopState)
    }
}