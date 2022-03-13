package app.services.model.control.state

import core.services.logger.Logger
import core.services.logger.Level
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

object PauseState : StoppableState() {
    private var resumeJob: Job? = null

    override fun resume(context: AgentModelControlContext) {
        resumeJob?.let { return }
        resumeJob = context.coroutineScope.launch {
            resumeModel(context)
            resumeJob = null
        }
    }

    private suspend fun resumeModel(context: AgentModelControlContext) {
        try {
            context.modelApi.resume()
            context.periodTaskExecutor.resume()
            context.onResume()
            context.setState(RunState)
        } catch (e: Exception) {
            disconnect(context)
            Logger.log(e.message.orEmpty(), Level.ERROR)
        }
    }

    override fun stop(context: AgentModelControlContext) {
        resumeJob?.cancel()
        super.stop(context)
    }

    override fun disconnect(context: AgentModelControlContext) {
        resumeJob?.cancel()
        super.disconnect(context)
    }
}