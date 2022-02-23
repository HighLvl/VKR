package core.api.dto

data class AgentSnapshot(val id: Int, val props: Map<String, Any>, val responses: List<Response>)
data class Response(val requestId: Int, val value: Any)
data class AgentBehaviour(val id: Int, val requests: List<Request>)
data class Request(val requestId: Int?, val name: String, val value: Any)
data class Snapshot(val time: Float, val agentSnapshots: List<AgentSnapshot>, val errors: List<Error>)
data class Error(val code: Int, val text: String)
data class Behaviour(val requests: List<AgentBehaviour>)
data class GlobalArgs(val args: Map<String, Any>)
enum class State {
    RUN, STOP, PAUSE
}
