package app.services.model.control.state

import core.services.logger.Logger
import core.services.logger.Level

object PauseState : StoppableState() {
    override fun resume(context: AgentModelControlContext) {
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
}