package app.services.model.control.state

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object PauseState : StoppableState() {
    override suspend fun resume(context: AgentModelControlContext) {
        withContext(Dispatchers.IO) { context.apiClient.resume() }
        context.periodTaskExecutor.resume()
        context.onResume()
        context.setState(RunState)
    }
}