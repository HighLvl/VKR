package app.components

import app.logger.Log
import app.logger.Logger
import app.services.model.configuration.RequestSignature
import com.google.common.base.Defaults
import core.components.AgentInterface
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance

class RequestBodies(
    private val requests: List<RequestSignature>,
    private val agentInterface: AgentInterface
) {
    private val _requestBodies: MutableList<RequestBody> = create()
    val bodies: List<RequestBody> = _requestBodies

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
        Logger.log("Commit $body", Log.Level.DEBUG)
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