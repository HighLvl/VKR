package app.services.model.control.state

import core.services.logger.Logger
import core.services.logger.Level

abstract class StoppableState : ConnectState() {
    override fun stop(context: AgentModelControlContext) {
        try {
            context.periodTaskExecutor.stop()
            context.modelApi.stop()
            context.onStop()
            context.setState(StopState)
        } catch (e: Exception) {
            disconnect(context)
            Logger.log(e.message.orEmpty(), Level.ERROR)
        }
    }
}