package app.requests

import app.api.dto.Error
import app.api.dto.Request
import app.api.dto.Requests
import app.api.dto.Responses
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import core.services.logger.Level
import core.services.logger.Logger
import kotlin.reflect.KClass

private data class RequestData(
    val agentId: Int,
    val name: String,
    val ack: Int,
    val args: List<Any> = listOf(),
    val resultClass: KClass<*>,
    val onResult: (Result<*>) -> Unit
)

object RequestMediator : RequestDispatcher, RequestSender {
    private var nextAck = 0
    private val ackRequestDataMap = mutableMapOf<Int, RequestData>()

    override fun <T : Any> sendRequest(
        agentId: Int,
        name: String,
        args: List<Any>,
        resultClass: KClass<T>,
        onResult: (Result<T>) -> Unit
    ) {
        val requestData = RequestData(agentId, name, nextAck, args, resultClass, onResult as (Result<*>) -> Unit)
        ackRequestDataMap[nextAck] = requestData
        if (!(agentId == 0 && name == "GetSnapshot")) Logger.log("Request scheduled: $requestData", Level.DEBUG)
        nextAck++
    }

    override fun commitRequests(): Requests {
        return Requests(ackRequestDataMap.map { (ack, requestData) ->
            Request(
                requestData.agentId,
                requestData.name,
                ack,
                requestData.args
            )
        })
    }

    override fun handleResponses(responses: Responses) {
        for (response in responses.responses) {
            val requestData = ackRequestDataMap[response.ack] ?: continue
            if (!(requestData.agentId == 0 && requestData.name == "GetSnapshot")) Logger.log(
                "Response received: $response",
                Level.DEBUG
            )

            when (response.success) {
                false -> onError(requestData.onResult, response.value as Error)
                true -> onSuccess(requestData.onResult, response.value)
            }
            ackRequestDataMap.remove(response.ack)
        }
    }

    override fun clear() {
        ackRequestDataMap.clear()
    }

    private fun onSuccess(onResult: (Result<*>) -> Unit, value: Any) {
        onResult(Result.success(value))
    }

    private fun onError(onResult: (Result<*>) -> Unit, error: Error) {
        Logger.log(
            "An error occurred in the model\ncode: ${error.code}, message: \"${error.text}\"",
            Level.ERROR
        )
        onResult(Result.failure<Any>(kotlin.runCatching { throw error }.exceptionOrNull()!!))
    }

    fun getTypeFor(ack: Int): KClass<*> {
        return ackRequestDataMap[ack]!!.resultClass
    }
}