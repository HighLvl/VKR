package app.components

import app.logger.Log
import app.logger.Logger
import app.services.model.configuration.MutableRequestSignature
import core.components.IgnoreInSnapshot
import core.components.Script
import core.components.AgentInterface
import core.components.Props
import core.api.dto.AgentSnapshot
import core.api.dto.Request
import core.components.SystemComponent
import app.utils.uppercaseFirstChar

class AgentInterface(
    @property:IgnoreInSnapshot
    val setterSignatures: List<MutableRequestSignature> = listOf(),
    @property:IgnoreInSnapshot
    val otherRequestSignatures: List<MutableRequestSignature> = listOf()
) : SystemComponent(), Script, AgentInterface {
    @IgnoreInSnapshot
    val requestBodies = RequestBodies(setterSignatures + otherRequestSignatures, this)

    override val id: Int
        get() = snapshot.id
    override val props: Props
        get() = _props
    private var _props: Props = Props(mapOf("abc" to "3", "fff" to 4, "a" to 6))

    private val _requests: MutableList<Request> = mutableListOf()

    @IgnoreInSnapshot
    var snapshot: AgentSnapshot = AgentSnapshot("", 0, mapOf(), listOf())
        set(value) {
            field = value
            updateProps(value.props)
        }

    fun commitRequests(): List<Request> {
        val committedRequests = _requests.toList()
        _requests.clear()
        return committedRequests
    }

    private fun updateProps(props: Map<String, Any>) {
        _props = Props(props)
    }

    override fun requestSetValue(varName: String, value: Any) {
        val requestName = "Set${varName.uppercaseFirstChar()}"
        request(requestName, value)
    }

    override fun request(name: String, value: Any) {
        val request = Request(null, name, value)
        Logger.log("Request scheduled: $request", Log.Level.INFO)
        _requests.add(request)
    }
}