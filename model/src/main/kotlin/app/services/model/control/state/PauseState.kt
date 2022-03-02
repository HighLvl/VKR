package app.services.model.control.state

import app.logger.Log
import app.logger.Logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object PauseState : StoppableState() {
    override suspend fun resume(context: AgentModelControlContext) {
        try {
            withContext(Dispatchers.IO) { context.apiClient.resume() }
            context.periodTaskExecutor.resume()
            context.onResume()
            context.setState(RunState)
        } catch (e: Exception) {
            disconnect(context)
            Logger.log(e.message.orEmpty(), Log.Level.ERROR)
        }
    }
}