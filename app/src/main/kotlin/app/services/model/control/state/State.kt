package app.services.model.control.state

sealed class State {
    open fun run(context: AgentModelControlContext, onResult: suspend (Result<Unit>) -> Unit = {}) {}
    open fun pause(context: AgentModelControlContext, onResult: suspend (Result<Unit>) -> Unit = {}) {}
    open fun resume(context: AgentModelControlContext, onResult: suspend (Result<Unit>) -> Unit = {}) {}
    open fun stop(context: AgentModelControlContext, onResult: suspend (Result<Unit>) -> Unit = {}) {}
    open fun update(context: AgentModelControlContext) {}
    open suspend fun connect(
        context: AgentModelControlContext,
        ip: String,
        port: Int
    ): Result<Unit> = Result.success(Unit)
}