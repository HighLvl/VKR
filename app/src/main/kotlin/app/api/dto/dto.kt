package app.api.dto

data class InputData(val requests: List<Request>)
data class Request(val agentId: Int, val name: String, val ack: Int, val args: List<Any>)
data class ModelInputArgs(val args: Map<String, Any>)

data class Snapshot(val t: Double, val agentSnapshots: List<AgentSnapshot>, val responses: List<Response>, val state: State)
data class AgentSnapshot(val type: String, val id: Int, val props: Map<String, Any>)
data class Response(val ack: Int, val result: Result<Any>)
data class Error(val code: Int, val text: String) : Exception()
enum class State {
    RUN, STOP, PAUSE
}