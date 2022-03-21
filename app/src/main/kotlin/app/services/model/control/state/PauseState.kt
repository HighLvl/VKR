package app.services.model.control.state

import app.services.model.control.state.AgentModelControlContext.ControlRequest.RESUME

object PauseState : StoppableState() {
    override fun resume(context: AgentModelControlContext) {
        context.sendControlRequest(RESUME)
        context.periodTaskExecutor.resume()
    }
}