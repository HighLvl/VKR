package app.services.model.control.state

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

abstract class ConnectState : State() {
    override suspend fun disconnect(context: AgentModelControlContext) {
        context.periodTaskExecutor.stop()
        withContext(Dispatchers.IO) { context.apiClient.disconnect() }
        context.setState(DisconnectState)
    }
}