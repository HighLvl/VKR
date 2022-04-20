package core.components.configuration

import core.components.base.Component

interface InputArgsComponent : Component {
    val inputArgs: Map<String, Any>
    fun put(name: String, value: Any)
    fun <T : Any> get(name: String): T
}