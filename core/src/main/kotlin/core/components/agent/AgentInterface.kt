package core.components.agent

import core.components.base.Component

interface AgentInterface: Component {
    val id: Int
    val props: Props
    fun requestSetValue(varName: String, value: Any)
    fun request(name: String, value: Map<String, Any>)
}

data class Props(private val props: Map<String, Any> = mapOf()) : Map<String, Any> by props

@Suppress("UNCHECKED_CAST")
fun <T> Props.getValue(name: String) = this[name] as T