package app.services.model.control.state

import app.services.model.control.state.AgentModelControlContext.ControlRequest.RUN

object StopState : ConnectState() {

    override fun run(context: AgentModelControlContext) {
        context.sendControlRequest(RUN)
        context.periodTaskExecutor.start()
    }
}