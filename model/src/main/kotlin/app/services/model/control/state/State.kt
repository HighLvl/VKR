package app.services.model.control.state

sealed class State {
    open suspend fun run(context: AgentModelControlContext, periodSec: Float) {}
    open suspend fun pause(context: AgentModelControlContext) {}
    open suspend fun resume(context: AgentModelControlContext) {}
    open suspend fun stop(context: AgentModelControlContext) {}
    open suspend fun connect(context: AgentModelControlContext, ip: String, port: Int) {}
    open suspend fun disconnect(context: AgentModelControlContext) {}
}