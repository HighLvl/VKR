package core.components.agent

import kotlin.reflect.KClass

interface AgentInterface {
    val id: Int
    val props: Props
    fun requestSetValue(
        varName: String,
        value: Any,
        onResult: suspend (Result<Unit>) -> Unit = {}
    )

    fun <T : Any> request(
        name: String,
        args: List<Any>,
        resultClass: KClass<T>,
        onResult: suspend (Result<T>) -> Unit = {}
    )
}

inline fun <reified T : Any> AgentInterface.request(
    name: String,
    args: List<Any>,
    noinline onResult: suspend (Result<T>) -> Unit = {}
) {
    request(name, args, T::class, onResult)
}

data class Props(private val props: Map<String, Any> = mapOf()) : Map<String, Any> by props

@Suppress("UNCHECKED_CAST")
fun <T> Props.getValue(name: String) = this[name] as T