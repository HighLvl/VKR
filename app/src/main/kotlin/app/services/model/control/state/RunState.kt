package app.services.model.control.state

import core.services.logger.Logger
import core.services.logger.Level
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

object RunState : StoppableState() {
    private var pauseJob: Job? = null

    override fun pause(context: AgentModelControlContext) {
        pauseJob?.let { return }
        context.periodTaskExecutor.pause()
        pauseJob = context.coroutineScope.launch {
            pauseModel(context)
            pauseJob = null
        }
    }

    private suspend fun pauseModel(context: AgentModelControlContext) {
        try {
            context.modelApi.pause()
            context.onPause()
            context.setState(PauseState)
        } catch (e: Exception) {
            disconnect(context)
            Logger.log(e.message.orEmpty(), Level.ERROR)
        }
    }

    override fun stop(context: AgentModelControlContext) {
        pauseJob?.cancel()
        super.stop(context)
    }

    override fun disconnect(context: AgentModelControlContext) {
        pauseJob?.cancel()
        super.disconnect(context)
    }
}