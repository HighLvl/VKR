package app.services.model.control.state

import app.logger.Log
import app.logger.Logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object RunState : StoppableState() {
    override suspend fun pause(context: AgentModelControlContext) {
        try {
            context.periodTaskExecutor.pause()
            withContext(Dispatchers.IO) { context.apiClient.pause() }
            context.onPause()
            context.setState(PauseState)
        } catch (e: Exception) {
            disconnect(context)
            Logger.log(e.message.orEmpty(), Log.Level.ERROR)
        }
    }
}