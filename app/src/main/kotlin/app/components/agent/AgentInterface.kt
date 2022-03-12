package app.components.agent

import app.api.dto.AgentSnapshot
import app.api.dto.Request
import app.components.base.SystemComponent
import core.components.agent.AgentInterface
import core.components.agent.Props
import core.components.base.AddInSnapshot
import core.components.base.Script
import core.components.configuration.RequestSignature
import core.services.logger.Level
import core.services.logger.Logger
import core.utils.uppercaseFirstChar

class AgentInterface : AgentInterface, SystemComponent, Script {
    val requestBodies = RequestBodies(this)
    private var _props: Props = Props()
    private val _requests: MutableList<Request> = mutableListOf()

    override val id: Int
        get() = snapshot.id

    @AddInSnapshot
    override val props: Props
        get() = _props

    var snapshot: AgentSnapshot = AgentSnapshot("", 0, mapOf(), listOf())
        set(value) {
            field = value
            updateProps(value.props)
        }

    fun setRequestSignatures(signatures: List<RequestSignature>) {
        requestBodies.setRequestSignatures(signatures)
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
        request(requestName, mapOf("value" to value))
    }

    override fun request(name: String, value: Map<String, Any>) {
        val request = Request(null, name, value)
        Logger.log("Request scheduled: $request", Level.INFO)
        _requests.add(request)
    }

    override fun onModelRun() {
        _requests.clear()
    }
}