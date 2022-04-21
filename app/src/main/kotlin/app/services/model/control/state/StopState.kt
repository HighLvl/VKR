package app.services.model.control.state

import app.services.model.control.state.AgentModelControlContext.ControlRequest.RUN

object StopState : ConnectState() {
    override fun run(context: AgentModelControlContext, onResult: suspend (Result<Unit>) -> Unit) {
        context.sendControlRequest(RUN, onResult)
        context.periodTaskExecutor.start()
    }
}