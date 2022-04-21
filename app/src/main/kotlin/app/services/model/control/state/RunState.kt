package app.services.model.control.state

import app.services.model.control.state.AgentModelControlContext.ControlRequest.PAUSE

object RunState : StoppableState() {
    override fun pause(context: AgentModelControlContext, onResult: suspend (Result<Unit>) -> Unit) {
        context.sendControlRequest(PAUSE, onResult)
    }

    override fun update(context: AgentModelControlContext) {
        context.sendControlRequest(AgentModelControlContext.ControlRequest.GET_SNAPSHOT)
    }
}