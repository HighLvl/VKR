package app.requests

interface RequestSender {
    fun <T : Any> sendRequest(agentId: Int, name: String, args: List<Any>, onResult: (Result<T>) -> Unit)
}