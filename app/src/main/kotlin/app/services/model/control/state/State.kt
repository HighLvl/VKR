package app.services.model.control.state

sealed class State {
    open fun run(context: AgentModelControlContext) {}
    open fun pause(context: AgentModelControlContext) {}
    open fun resume(context: AgentModelControlContext) {}
    open fun stop(context: AgentModelControlContext) {}
    open fun update(context: AgentModelControlContext) {}
    open suspend fun connect(context: AgentModelControlContext, ip: String, port: Int) {}
}