package app.components

import app.logger.Log
import app.logger.Logger
import app.services.model.`interface`.RequestSignature
import core.components.base.IgnoreInSnapshot
import core.components.base.Script
import core.entities.AgentInterface
import core.entities.Props
import core.api.dto.AgentSnapshot
import core.api.dto.Request
import core.uppercaseFirstChar

class AgentInterface(
    @property:IgnoreInSnapshot
    val setterSignatures: List<RequestSignature> = listOf(),
    @property:IgnoreInSnapshot
    val otherRequestSignatures: List<RequestSignature> = listOf()
) : SystemComponent(), Script, AgentInterface {
    @IgnoreInSnapshot
    val requestBodies = RequestBodies(setterSignatures + otherRequestSignatures, this)

    override val id: Int
        get() = snapshot.id
    override val props: Props
        get() = _props
    private var _props: Props = Props(mapOf("abc" to "3", "fff" to 4, "a" to 6))

    @IgnoreInSnapshot
    val requests: List<Request>
        get() = _requests
    private val _requests: MutableList<Request> = mutableListOf()
    private val propsName by lazy { this::props.name }

    @IgnoreInSnapshot
    var snapshot: AgentSnapshot = AgentSnapshot(0, mapOf(), listOf())
        set(value) {
            field = value
            updateProps(value.props)
        }

    private fun updateProps(props: Map<String, Any>) {
        _props = Props(props)
    }

    override fun requestSetProp(varName: String, value: Any) {
        val requestName = "Set${varName.uppercaseFirstChar()}"
        request(requestName, value)
    }

    override fun request(name: String, value: Any) {
        val request = Request(null, name, value)
        Logger.log("Request scheduled: ${request.toString()}", Log.Level.INFO)
        _requests.add(request)
    }

    override fun afterUpdate() {
        _requests.clear()
    }
}