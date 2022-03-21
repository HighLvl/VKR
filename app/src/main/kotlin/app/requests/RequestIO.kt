package app.requests

data class Request(val agentId: Int, val name: String, val ack: Int, val args: List<Any> = listOf())
data class Response(val ack: Int, val result: Result<Any>)

interface RequestIO {
    fun commitRequests(): List<Request>
    fun handleResponses(responses: List<Response>)
}