package core.components

interface AgentInterface {
    val id: Int
    val props: Props
    fun requestSetValue(varName: String, value: Any)
    fun request(name: String, value: Any)
}

data class Props(val props: Map<String, Any> = mapOf()) : Map<String, Any> by props

@Suppress("UNCHECKED_CAST")
fun <T> Props.getValue(name: String) = this[name] as T