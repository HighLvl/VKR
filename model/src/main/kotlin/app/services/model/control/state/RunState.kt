package app.services.model.control.state

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object RunState : StoppableState() {
    override suspend fun pause(context: AgentModelControlContext) {
        context.periodTaskExecutor.pause()
        withContext(Dispatchers.IO) { context.apiClient.pause() }
        context.onPause()
        context.setState(PauseState)
    }
}