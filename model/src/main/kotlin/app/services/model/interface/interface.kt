package app.services.model.`interface`

import core.uppercaseFirstChar
import kotlin.reflect.KClass

class ModelInterface {
    private val _agentInterfaces = mutableSetOf<AgentInterface>()
    val agentInterfaces: Set<AgentInterface> = _agentInterfaces

    fun addAgentInterface(agentInterface: AgentInterface) {
        _agentInterfaces.add(agentInterface)
    }
}

data class AgentInterface(val name: String) {
    private val _requests = mutableMapOf<String, RequestSignature>()
    val requestSignatures: Collection<RequestSignature> = _requests.values
    private val _setters = mutableMapOf<String, RequestSignature>()
    val setters: Collection<RequestSignature> = _setters.values

    fun addRequest(requestSignature: RequestSignature) {
        _requests[requestSignature.name] = requestSignature
    }

    fun addSetter(setter: RequestSignature) {
        _setters[setter.name] = setter
    }
}

data class RequestSignature(val name: String, val returnType: KClass<*>) {
    private val _params = mutableMapOf<String, KClass<*>>()
    val params: Map<String, KClass<*>> = _params

    fun addParam(name: String, type: KClass<*>) {
        _params[name] = type
    }
}

fun modelInterface(builder: ModelInterface.() -> Unit) = ModelInterface().apply(builder)
fun ModelInterface.agentInterface(name: String, builder: AgentInterface.() -> Unit) =
    addAgentInterface(AgentInterface(name).apply(builder))

inline fun <reified T> AgentInterface.request(name: String, builder: RequestSignature.() -> Unit = {}) =
    addRequest(RequestSignature(name, T::class).apply(builder))

inline fun <reified T> AgentInterface.setter(varName: String) =
    addSetter(RequestSignature("Set${varName.uppercaseFirstChar()}", Unit::class).apply { addParam("value", T::class) })

inline fun <reified T> RequestSignature.param(name: String) = addParam(name, T::class)
