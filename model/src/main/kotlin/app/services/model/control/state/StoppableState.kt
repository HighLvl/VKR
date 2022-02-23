package app.services.model.control.state

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

abstract class StoppableState: ConnectState() {
    override suspend fun stop(context: AgentModelControlContext) {
        context.periodTaskExecutor.stop()
        withContext(Dispatchers.IO) { context.apiClient.stop() }
        context.onStop()
        context.setState(StopState)
    }
}