package core.components.configuration

import kotlin.reflect.KClass

interface ModelConfiguration {
    val agentInterfaces: Map<String, AgentInterface>
    val inputArgs: Map<String, Any>
}

class MutableModelConfiguration : ModelConfiguration {
    private val _agentInterfaces = mutableSetOf<MutableAgentInterface>()
    override val agentInterfaces: Map<String, MutableAgentInterface> by lazy {
        _agentInterfaces.associateBy(MutableAgentInterface::agentType)
    }
    private val _inputArgs = mutableMapOf<String, Any>()
    override val inputArgs: Map<String, Any> = _inputArgs

    fun addAgentInterface(agentInterface: MutableAgentInterface) {
        _agentInterfaces.add(agentInterface)
    }

    fun putInputArg(name: String, value: Any) {
        _inputArgs[name] = value
    }

    override fun toString(): String {
        return agentInterfaces.toString()
    }
}

data class Property(val name: String)

interface AgentInterface {
    val agentType: String
    val properties: List<Property>
    val otherRequests: Collection<MutableRequestSignature>
    val setters: Collection<MutableRequestSignature>
}

data class MutableAgentInterface(override val agentType: String) : AgentInterface {
    private val _properties = mutableListOf<Property>()
    private val _setters = mutableMapOf<String, MutableRequestSignature>()
    private val _requests = mutableMapOf<String, MutableRequestSignature>()

    override val properties: List<Property> = _properties
    override val otherRequests: Collection<MutableRequestSignature> = _requests.values
    override val setters: Collection<MutableRequestSignature> = _setters.values

    fun addRequest(requestSignature: MutableRequestSignature) {
        _requests[requestSignature.name] = requestSignature
    }

    fun addSetter(setter: MutableRequestSignature) {
        _setters[setter.name] = setter
    }

    fun addProperty(prop: Property) {
        _properties += prop
    }
}

interface RequestSignature {
    val name: String
    val returnType: KClass<*>
    val params: Map<String, KClass<*>>
}

data class MutableRequestSignature(override val name: String, override val returnType: KClass<*>) : RequestSignature {
    private val _params = mutableMapOf<String, KClass<*>>()
    override val params: Map<String, KClass<*>> = _params

    fun addParam(name: String, type: KClass<*>) {
        _params[name] = type
    }
}