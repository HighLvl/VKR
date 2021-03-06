package app.services.model.control.state

import app.services.model.control.state.AgentModelControlContext.ControlRequest.STOP

abstract class StoppableState : ConnectState() {
    override fun stop(context: AgentModelControlContext, onResult: suspend (Result<Unit>) -> Unit) {
        context.sendControlRequest(STOP, onResult)
    }
}