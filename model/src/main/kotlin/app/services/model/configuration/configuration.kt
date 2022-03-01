package app.services.model.configuration

import app.utils.uppercaseFirstChar
import kotlin.reflect.KClass

interface ModelConfiguration {
    val agentInterfaces: Map<String, MutableAgentInterface>
    val globalArgs: Map<String, Any>
}

class MutableModelConfiguration: ModelConfiguration {
    private val _agentInterfaces = mutableSetOf<MutableAgentInterface>()
    override val agentInterfaces: Map<String, MutableAgentInterface> by lazy {
        _agentInterfaces.associateBy(MutableAgentInterface::agentType)
    }
    private val _globalArgs = mutableMapOf<String, Any>()
    override val globalArgs: Map<String, Any> = _globalArgs

    fun addAgentInterface(agentInterface: MutableAgentInterface) {
        _agentInterfaces.add(agentInterface)
    }

    fun putGlobalArg(name: String, value: Any) {
        _globalArgs[name] = value
    }

    override fun toString(): String {
        return agentInterfaces.toString()
    }
}

interface AgentInterface {
    val agentType: String
    val requestSignatures: Collection<MutableRequestSignature>
    val setters: Collection<MutableRequestSignature>

}
data class MutableAgentInterface(override val agentType: String): AgentInterface {
    private val _requests = mutableMapOf<String, MutableRequestSignature>()
    override val requestSignatures: Collection<MutableRequestSignature> = _requests.values
    private val _setters = mutableMapOf<String, MutableRequestSignature>()
    override val setters: Collection<MutableRequestSignature> = _setters.values

    fun addRequest(requestSignature: MutableRequestSignature) {
        _requests[requestSignature.name] = requestSignature
    }

    fun addSetter(setter: MutableRequestSignature) {
        _setters[setter.name] = setter
    }
}

interface RequestSignature {
    val name: String
    val returnType: KClass<*>
    val params: Map<String, KClass<*>>
}

data class MutableRequestSignature(override val name: String, override val returnType: KClass<*>): RequestSignature {
    private val _params = mutableMapOf<String, KClass<*>>()
    override val params: Map<String, KClass<*>> = _params

    fun addParam(name: String, type: KClass<*>) {
        _params[name] = type
    }
}

class ModelConfigurationContext(private val modelConfiguration: MutableModelConfiguration) {
    fun agentInterface(name: String, builder: AgentInterfaceContext.() -> Unit) {
        modelConfiguration.addAgentInterface(
            MutableAgentInterface(name).apply { AgentInterfaceContext(this).apply(builder) }
        )
    }

    fun globalArg(name: String, value: Any) {
        modelConfiguration.putGlobalArg(name, value)
    }

    fun globalArgs(vararg args: Pair<String, Any>) {
        args.forEach { modelConfiguration.putGlobalArg(it.first, it.second) }
    }
}

class AgentInterfaceContext(private val agentInterface: MutableAgentInterface) {
    fun <T : Any> request(returnType: KClass<T>, name: String, builder: RequestSignatureContext.() -> Unit = {}) {
        agentInterface.addRequest(
            MutableRequestSignature(
                name,
                returnType
            ).apply { RequestSignatureContext(this).apply(builder) })
    }

    fun <T : Any> setter(valueType: KClass<T>, varName: String) =
        agentInterface.addSetter(
            MutableRequestSignature(
                "Set${varName.uppercaseFirstChar()}",
                Unit::class
            ).apply { addParam("value", valueType) })

    inline fun <reified T : Any> request(name: String, noinline builder: RequestSignatureContext.() -> Unit = {}) =
        request(T::class, name, builder)

    inline fun <reified T : Any> setter(varName: String) = setter(T::class, varName)
}

class RequestSignatureContext(private val requestSignature: MutableRequestSignature) {
    fun <T : Any> param(paramType: KClass<T>, name: String) = requestSignature.addParam(name, paramType)
    inline fun <reified T : Any> param(name: String) = param(T::class, name)
}

fun modelConfiguration(builder: ModelConfigurationContext.() -> Unit): ModelConfiguration =
    MutableModelConfiguration().apply { ModelConfigurationContext(this).apply(builder) }




