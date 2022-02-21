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

class ModelInterfaceContext(private val modelInterface: ModelInterface) {
    fun agentInterface(name: String, builder: AgentInterfaceContext.() -> Unit) {
        modelInterface.addAgentInterface(
            AgentInterface(name).apply { AgentInterfaceContext(this).apply(builder) }
        )
    }
}

class AgentInterfaceContext(private val agentInterface: AgentInterface) {
    fun <T : Any> request(returnType: KClass<T>, name: String, builder: RequestSignatureContext.() -> Unit = {}) {
        agentInterface.addRequest(
            RequestSignature(
                name,
                returnType
            ).apply { RequestSignatureContext(this).apply(builder) })
    }

    fun <T : Any> setter(valueType: KClass<T>, varName: String) =
        agentInterface.addSetter(
            RequestSignature(
                "Set${varName.uppercaseFirstChar()}",
                Unit::class
            ).apply { addParam("value", valueType) })

    inline fun <reified T : Any> request(name: String, noinline builder: RequestSignatureContext.() -> Unit = {}) =
        request(T::class, name, builder)

    inline fun <reified T : Any> setter(varName: String) = setter(T::class, varName)
}

class RequestSignatureContext(private val requestSignature: RequestSignature) {
    fun <T : Any> param(paramType: KClass<T>, name: String) = requestSignature.addParam(name, paramType)
    inline fun <reified T : Any> param(name: String) = param(T::class, name)
}

fun modelInterface(builder: ModelInterfaceContext.() -> Unit) =
    ModelInterface().apply { ModelInterfaceContext(this).apply(builder) }




