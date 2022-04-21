package core.components.configuration

interface InputArgs {
    val inputArgs: Map<String, Any>
    fun put(name: String, value: Any)
    fun <T : Any> get(name: String): T
}