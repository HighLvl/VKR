package app.components

import core.components.base.IgnoreInSnapshot
import core.components.base.Script
import core.entities.AgentInterface
import core.entities.Props
import core.api.dto.AgentSnapshot
import core.api.dto.Request
import core.uppercaseFirstChar

class AgentInterfaceScript : Script(), AgentInterface {
    override val id: Int
        get() = snapshot.id
    override val props: Props
        get() = _props
    private lateinit var _props: Props

    @IgnoreInSnapshot
    val requests: List<Request>
        get() = _requests
    private val _requests: MutableList<Request> = mutableListOf()

    @IgnoreInSnapshot
    var snapshot: AgentSnapshot = AgentSnapshot(0, mapOf(), listOf())
        set(value) {
            field = value
            updateProps(value.props)
        }

    var a = 0

    private fun updateProps(propsMap: Map<String, Any>) {
        _props = propsMap
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