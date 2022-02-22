package app.components

import app.services.model.`interface`.RequestSignature
import com.google.common.base.Defaults
import core.components.base.IgnoreInSnapshot
import core.components.base.Script
import core.entities.AgentInterface
import core.entities.Props
import core.api.dto.AgentSnapshot
import core.api.dto.Request
import core.components.base.ComponentSnapshot
import core.components.base.Property
import core.components.changePropertyValue
import core.uppercaseFirstChar
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance

class AgentInterface(
    @property:IgnoreInSnapshot
    val setterSignatures: List<RequestSignature> = listOf(),
    @property:IgnoreInSnapshot
    val otherRequestSignatures: List<RequestSignature> = listOf()
) : SystemComponent(), Script, AgentInterface {
    @IgnoreInSnapshot
    val requestBodies = RequestBodies(setterSignatures, this)

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

    override fun requestSetProp(name: String, value: Any) {
        val requestName = "Set${name.uppercaseFirstChar()}"
        val request = Request(null, requestName, value)
        _requests.add(request)
    }

    override fun afterUpdate() {
        _requests.clear()
    }
}