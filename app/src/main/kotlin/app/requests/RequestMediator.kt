package app.requests

import core.services.logger.Level
import core.services.logger.Logger

class RequestMediator: RequestIO, RequestSender {
    private var nextAck = 0
    private val ackOnResultMap = mutableMapOf<Int, (Result<Any>) -> Unit>()
    private val requests = mutableListOf<Request>()

    override fun <T : Any> sendRequest(agentId: Int, name: String, args: List<Any>, onResult: (Result<T>) -> Unit) {
        val request = Request(agentId, name, nextAck, args)
        requests += request
        ackOnResultMap[nextAck] = onResult as (Result<Any>) -> Unit
        Logger.log("Request scheduled: $request", Level.DEBUG)
        nextAck++
    }

    override fun commitRequests(): List<Request> {
        return requests.toList().also { requests.clear() }
    }

    override fun handleResponses(responses: List<Response>) {
        for (response in responses) {
            Logger.log("Response received: $response", Level.DEBUG)
            val onResult = ackOnResultMap.remove(response.ack) ?: continue
            onResult(response.result)
        }
    }
}