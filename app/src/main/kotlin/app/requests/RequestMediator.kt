package app.requests

import core.services.logger.Level
import core.services.logger.Logger
import kotlin.reflect.KClass
import kotlin.reflect.full.isSubclassOf

class RequestMediator : RequestIO, RequestSender {
    private var nextAck = 0
    private val ackOnResultMap = mutableMapOf<Int, Pair<KClass<*>, (Result<Any>) -> Unit>>()
    private val requests = mutableListOf<Request>()

    override fun <T : Any> sendRequest(
        agentId: Int,
        name: String,
        args: List<Any>,
        resultClass: KClass<T>,
        onResult: (Result<T>) -> Unit
    ) {
        val request = Request(agentId, name, nextAck, args)
        requests += request
        ackOnResultMap[nextAck] = resultClass to (onResult as (Result<Any>) -> Unit)
        Logger.log("Request scheduled: $request", Level.DEBUG)
        nextAck++
    }

    override fun commitRequests(): List<Request> {
        return requests.toList().also { requests.clear() }
    }

    override fun handleResponses(responses: List<Response>) {
        for (response in responses) {
            Logger.log("Response received: $response", Level.DEBUG)
            val (kClass, onResult) = ackOnResultMap.remove(response.ack) ?: continue
            if (kClass.isSubclassOf(Unit::class)) {
                if (response.result.isSuccess) {
                    onResult(Result.success(Unit))
                }
                else {
                    onResult(response.result)
                }
                continue
            }
            onResult(response.result)
        }
    }
}