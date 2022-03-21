package app.requests

import kotlin.reflect.KClass

interface RequestSender {
    fun <T : Any> sendRequest(agentId: Int, name: String, args: List<Any>, resultClass: KClass<T>, onResult: (Result<T>) -> Unit)
}

inline fun <reified T : Any> RequestSender.sendRequest(
    agentId: Int,
    name: String,
    args: List<Any>,
    noinline onResult: (Result<T>) -> Unit
) {
    sendRequest(agentId, name, args, T::class,  onResult)
}