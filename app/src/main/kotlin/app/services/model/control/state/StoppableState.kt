package app.services.model.control.state

import core.services.logger.Logger
import core.services.logger.Level
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

abstract class StoppableState : ConnectState() {
    private var stopJob: Job? = null

    override fun stop(context: AgentModelControlContext) {
        stopJob?.let { return }
        context.periodTaskExecutor.stop()
        stopJob = context.coroutineScope.launch {
            stopModel(context)
            stopJob = null
        }
    }

    private suspend fun stopModel(context: AgentModelControlContext) {
        try {
            context.modelApi.stop()
            context.onStop()
            context.setState(StopState)
        } catch (e: Exception) {
            disconnect(context)
            Logger.log(e.message.orEmpty(), Level.ERROR)
        }
    }

    override fun disconnect(context: AgentModelControlContext) {
        stopJob?.cancel()
        super.disconnect(context)
    }
}