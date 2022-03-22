package app.api.dto

import app.api.ResponsesDeserializer
import com.fasterxml.jackson.databind.annotation.JsonDeserialize

data class Requests(val requests: List<Request>)
data class Request(val agentId: Int, val name: String, val ack: Int, val args: List<Any>)

@JsonDeserialize(using = ResponsesDeserializer::class)
data class Responses(val responses: List<Response> = listOf())
data class Response(val ack: Int = 0, val success: Boolean = true, val value: Any = Any())
data class Error(val code: Int = 0, val text: String = "") : Exception()

data class Snapshot(val t: Double = 0.0, val agentSnapshots: Map<String, List<AgentSnapshot>> = mapOf())
data class AgentSnapshot(val id: Int = 0, val props: Map<String, Any> = mapOf())

enum class State {
    RUN, STOP, PAUSE
}

data class ModelInputArgs(val args: Map<String, Any>)


