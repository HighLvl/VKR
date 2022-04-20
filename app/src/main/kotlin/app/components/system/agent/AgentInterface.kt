package app.components.system.agent

import app.api.dto.AgentSnapshot
import app.components.system.base.Native
import app.requests.RequestSender
import core.components.agent.AgentInterface
import core.components.agent.Props
import core.components.base.AddInSnapshot
import core.components.base.Script
import core.components.configuration.RequestSignature
import core.utils.uppercaseFirstChar
import kotlin.reflect.KClass

class AgentInterface : Script(), AgentInterface, Native {
    val requestBodies = RequestBodies(this)
    private var _props: Props = Props()
    private lateinit var requestSender: RequestSender

    override val id: Int
        get() = snapshot.id

    @AddInSnapshot
    override val props: Props
        get() = _props

    var snapshot: AgentSnapshot = AgentSnapshot(0, mapOf())
        set(value) {
            field = value
            updateProps(value.props)
        }

    fun setRequestSignatures(signatures: List<RequestSignature>) {
        requestBodies.setRequestSignatures(signatures)
    }

    fun setRequestSender(requestSender: RequestSender) {
        this.requestSender = requestSender
    }

    private fun updateProps(props: Map<String, Any>) {
        _props = Props(props)
    }

    override fun requestSetValue(varName: String, value: Any, onResult: (Result<Unit>) -> Unit) {
        val requestName = "Set${varName.uppercaseFirstChar()}"
        request(requestName, listOf(value), Unit::class, onResult)
    }

    override fun <T : Any> request(
        name: String,
        args: List<Any>,
        resultClass: KClass<T>,
        onResult: (Result<T>) -> Unit
    ) {
        requestSender.sendRequest(id, name, args, resultClass, onResult)
    }
}