package core.components.agent

import core.components.base.Component

interface AgentInterface: Component {
    val id: Int
    val props: Props
    fun requestSetValue(varName: String, value: Any, onResult: (Result<Unit>) -> Unit = {})
    fun <T: Any> request(name: String, args: List<Any>, onResult: (Result<T>) -> Unit = {})
}

data class Props(private val props: Map<String, Any> = mapOf()) : Map<String, Any> by props

@Suppress("UNCHECKED_CAST")
fun <T> Props.getValue(name: String) = this[name] as T