package core.entities

interface AgentInterface {
    val id: Int
    val props: Props
    fun requestSetProp(name: String, value: Any)
}

typealias Props = Map<String, Any>

@Suppress("UNCHECKED_CAST")
fun <T> Props.getValue(name: String) = this[name] as T