package core.components

abstract class AgentInterface: SystemComponent {
    abstract val id: Int
    abstract val props: Props
    abstract fun requestSetValue(varName: String, value: Any)
    abstract fun request(name: String, value: Map<String, Any>)
}

data class Props(private val props: Map<String, Any> = mapOf()) : Map<String, Any> by props

@Suppress("UNCHECKED_CAST")
fun <T> Props.getValue(name: String) = this[name] as T