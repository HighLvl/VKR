package app.components

import core.services.logger.Logger
import app.services.model.configuration.MutableRequestSignature
import app.services.model.configuration.RequestSignature
import com.google.common.base.Defaults
import core.services.logger.Level
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance

class RequestBodies(
    private val agentInterface: AgentInterface
) {
    private val requests = mutableListOf<RequestSignature>()
    private val _requestBodies = mutableListOf<RequestBody>()
    val bodies: List<RequestBody> = _requestBodies

    fun setRequestSignatures(requests: List<RequestSignature>) {
        with(this.requests) {
            clear()
            addAll(requests)
        }
        with(_requestBodies) {
            clear()
            addAll(create())
        }
    }

    private fun create(): MutableList<RequestBody> = requests.map { signature ->
        RequestBody(
            signature.name,
            signature.params.map { param ->
                param.key to Pair(param.value.getDefaultValue(), param.value.java)
            }.toMap()
        )
    }.toMutableList()

    fun commit(name: String) {
        val body = bodies.firstOrNull { it.name == name } ?: return
        Logger.log("Commit $body", Level.DEBUG)
        agentInterface.request(body.name, body.args.map { it.key to it.value.first }.toMap())
    }

    fun changeRequestBody(newBody: RequestBody) {
        val oldBodyIndex = _requestBodies.indexOfFirst { newBody.name == it.name }
        if (oldBodyIndex == -1) return
        _requestBodies[oldBodyIndex] = newBody
    }

    private fun KClass<*>.getDefaultValue() = when (javaPrimitiveType) {
        null -> createInstanceOrNull()
        else -> Defaults.defaultValue(javaPrimitiveType)
    } ?: Unit


    private fun KClass<*>.createInstanceOrNull() = try {
        createInstance()
    } catch (e: Exception) {
        null
    }
}