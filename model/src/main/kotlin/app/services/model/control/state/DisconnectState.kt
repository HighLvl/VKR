package app.services.model.control.state

import core.services.logger.Level
import core.services.logger.Logger

object DisconnectState : State() {
    override fun connect(context: AgentModelControlContext, ip: String, port: Int) {
        try {
            when (context.modelApi.connect(ip, port)) {
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
            Logger.log(e.message.orEmpty(), Level.ERROR)
        }
    }
}