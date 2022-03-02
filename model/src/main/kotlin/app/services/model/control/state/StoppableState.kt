package app.services.model.control.state

import app.logger.Log
import app.logger.Logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

abstract class StoppableState : ConnectState() {
    override suspend fun stop(context: AgentModelControlContext) {
        try {
            context.periodTaskExecutor.stop()
            context.behaviourRequestsReadyDisposable?.dispose()
            withContext(Dispatchers.IO) { context.apiClient.stop() }
            context.onStop()
            context.setState(StopState)
        } catch (e: Exception) {
            disconnect(context)
            Logger.log(e.message.orEmpty(), Log.Level.ERROR)
        }
    }
}