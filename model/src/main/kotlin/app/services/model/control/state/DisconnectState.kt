package app.services.model.control.state

import app.logger.Log
import app.logger.Logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object DisconnectState : State() {
    override suspend fun connect(context: AgentModelControlContext, ip: String, port: Int) {
        try {
            val state = withContext(Dispatchers.IO) {
                context.apiClient.connect(ip, port)
            }
            when (state) {
                core.api.dto.State.RUN -> {
                    StopState.restoreRun(context)
                }
                core.api.dto.State.PAUSE -> {
                    StopState.restorePause(context)
                }
                core.api.dto.State.STOP -> {
                    context.setState(StopState)
                }
            }
        } catch (e: Exception) {
            context.setState(DisconnectState)
            Logger.log(e.message.orEmpty(), Log.Level.ERROR)
        }
    }
}