package app.services.model.control.state

import core.services.logger.Level
import core.services.logger.Logger
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.take
import java.util.concurrent.CancellationException

object DisconnectState : State() {
    override suspend fun connect(
        context: AgentModelControlContext,
        ip: String,
        port: Int
    ): Result<Unit> {
        return try {
            tryConnect(context, ip, port)
        } catch (e: Exception) {
            context.setState(DisconnectState)
            if (e !is CancellationException) {
                Logger.log(e.message.orEmpty(), Level.ERROR)
            }
            Result.failure(e)
        }
    }

    private suspend fun tryConnect(
        context: AgentModelControlContext,
        ip: String,
        port: Int
    ): Result<Unit> {
        val result = connectAndWaitState(context, ip, port)
        return when {
            result.isSuccess -> {
                context.onConnect(result.getOrThrow())
                Result.success(Unit)
            }
            result.isFailure -> {
                context.emitCurrentState()
                Result.failure(result.exceptionOrNull()!!)
            }
            else -> throw IllegalStateException()
        }
    }

    private suspend fun connectAndWaitState(
        context: AgentModelControlContext,
        ip: String,
        port: Int
    ) = MutableSharedFlow<Result<app.api.dto.State>>().apply {
        context.modelApi.connect(ip, port)
        context.sendGetStateRequest { result ->
            emit(result)
        }
        context.periodTaskExecutor.start()
    }.take(1).last()
}