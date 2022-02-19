package app.services.model.`interface`
import kotlin.reflect.KClass

class ModelInterface {
    private val _agentInterfaces = mutableSetOf<AgentInterface>()
    val agentInterfaces: Set<AgentInterface> = _agentInterfaces

    fun addAgentInterface(agentInterface: AgentInterface) {
        _agentInterfaces.add(agentInterface)
    }
}

data class AgentInterface(val name: String) {
    private val _requests = mutableMapOf<String, Request>()
    val requests: Collection<Request> = _requests.values
    private val _setters = mutableMapOf<String, Request>()
    val setters: Collection<Request> = _setters.values

    fun addRequest(request: Request) {
        _requests[request.name] = request
    }

    fun addSetter(setter: Request) {
        _setters[setter.name] = setter
    }
}

data class Request(val name: String, val returnType: KClass<*>) {
    private val _params = mutableMapOf<String, KClass<*>>()
    val params: Map<String, KClass<*>> = _params

    fun addParam(name: String, type: KClass<*>) {
        _params[name] = type
    }
}

fun modelInterface(builder: ModelInterface.() -> Unit) = ModelInterface().apply(builder)
fun ModelInterface.agentInterface(name: String, builder: AgentInterface.() -> Unit) =
    addAgentInterface(AgentInterface(name).apply(builder))

inline fun <reified T> AgentInterface.request(name: String, builder: Request.() -> Unit = {}) =
    addRequest(Request(name, T::class).apply(builder))

inline fun <reified T> AgentInterface.setter(varName: String) =
    addSetter(Request(varName, Unit::class).apply { addParam("value", T::class) })

inline fun <reified T> Request.param(name: String) = addParam(name, T::class)
