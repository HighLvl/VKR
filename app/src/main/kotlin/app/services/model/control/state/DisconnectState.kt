package app.services.model.control.state

import core.services.logger.Level
import core.services.logger.Logger
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

object DisconnectState : State() {
    private var connectJob: Job? = null
    override fun connect(context: AgentModelControlContext, ip: String, port: Int) {
        connectJob?.let { return }
        connectJob = context.coroutineScope.launch {
            connectToModel(context, ip, port)
            connectJob = null
        }
    }

    private suspend fun connectToModel(
        context: AgentModelControlContext,
        ip: String,
        port: Int
    ) {
        try {
            when (context.modelApi.connect(ip, port)) {
                app.api.dto.State.RUN -> {
                    StopState.restoreRun(context)
                }
                app.api.dto.State.PAUSE -> {
                    StopState.restorePause(context)
                }
                app.api.dto.State.STOP -> {
                    context.setState(StopState)
                }
            }
        } catch (e: Exception) {
            context.setState(DisconnectState)
            Logger.log(e.message.orEmpty(), Level.ERROR)
        }
    }
}