package app.services.model.control.state

sealed class State {
    open fun run(context: AgentModelControlContext, periodSec: Float) {}
    open fun pause(context: AgentModelControlContext) {}
    open fun resume(context: AgentModelControlContext) {}
    open fun stop(context: AgentModelControlContext) {}
    open fun connect(context: AgentModelControlContext, ip: String, port: Int) {}
    open fun disconnect(context: AgentModelControlContext) {
        context.periodTaskExecutor.stop()
        context.modelApi.disconnect()
        context.setState(DisconnectState)
    }
}