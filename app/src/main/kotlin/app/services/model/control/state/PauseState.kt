package app.services.model.control.state

import app.services.model.control.state.AgentModelControlContext.ControlRequest.RESUME

object PauseState : StoppableState() {
    override fun resume(context: AgentModelControlContext, onResult: (Result<Unit>) -> Unit) {
        context.sendControlRequest(RESUME, onResult)
        context.periodTaskExecutor.resume()
    }

    override fun stop(context: AgentModelControlContext, onResult: (Result<Unit>) -> Unit) {
        super.stop(context, onResult)
        context.periodTaskExecutor.resume()
    }
}