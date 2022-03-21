package app.api.dto

import app.api.ResponseDeserializer
import com.fasterxml.jackson.databind.annotation.JsonDeserialize


data class InputData(val requests: List<Request>)
data class Request(val agentId: Int, val name: String, val ack: Int, val args: List<Any>)

data class Snapshot(
    val t: Double = 0.0,
    val agentSnapshots: Map<String, List<AgentSnapshot>> = mapOf(),
    val responses: List<Response> = listOf(),
    val state: State = State.STOP
)

enum class State {
    RUN, STOP, PAUSE
}

data class AgentSnapshot(val id: Int = 0, val props: Map<String, Any> = mapOf())

@JsonDeserialize(using = ResponseDeserializer::class)
data class Response(val ack: Int = 0, val success: Boolean = true, val value: Any = Any())
data class Error(val code: Int = 0, val text: String = "") : Exception()

data class ModelInputArgs(val args: Map<String, Any>)


