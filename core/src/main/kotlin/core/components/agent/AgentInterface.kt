package core.components.agent

import core.components.base.Component
import kotlin.reflect.KClass

interface AgentInterface: Component {
    val id: Int
    val props: Props
    fun requestSetValue(varName: String, value: Any, onResult: (Result<Unit>) -> Unit = {})
    fun <T: Any> request(name: String, args: List<Any>, resultClass: KClass<T>, onResult: (Result<T>) -> Unit = {})
}

inline fun <reified T: Any> AgentInterface.request(name: String, args: List<Any>, noinline onResult: (Result<T>) -> Unit = {}) {
    request(name, args, T::class, onResult)
}

data class Props(private val props: Map<String, Any> = mapOf()) : Map<String, Any> by props

@Suppress("UNCHECKED_CAST")
fun <T> Props.getValue(name: String) = this[name] as T