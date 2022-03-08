package app.services.model.control.state

import core.services.logger.Logger
import core.services.logger.Level

object RunState : StoppableState() {
    override fun pause(context: AgentModelControlContext) {
        try {
            context.periodTaskExecutor.pause()
            context.modelApi.pause()
            context.onPause()
            context.setState(PauseState)
        } catch (e: Exception) {
            disconnect(context)
            Logger.log(e.message.orEmpty(), Level.ERROR)
        }
    }
}