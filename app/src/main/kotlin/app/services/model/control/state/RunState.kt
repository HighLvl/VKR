package app.services.model.control.state

import app.services.model.control.state.AgentModelControlContext.ControlRequest.PAUSE

object RunState : StoppableState() {
    override fun pause(context: AgentModelControlContext) {
        context.sendControlRequest(PAUSE)
    }
}