package app.components

import core.components.base.IgnoreInSnapshot
import core.components.base.Script
import core.entities.AgentInterface
import core.entities.Props
import core.api.dto.AgentSnapshot
import core.api.dto.Request
import core.components.base.ComponentSnapshot
import core.components.base.Property
import core.uppercaseFirstChar

class AgentInterface : Script(), AgentInterface {
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

    var a = 0

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

    override fun getSnapshot(): ComponentSnapshot {
        val snapshot = super.getSnapshot()
        val immutableProps = snapshot.immutableProps as MutableList<Property>
        val props = immutableProps.removeAt(immutableProps.indexOfFirst { it.name == propsName })
        immutableProps.addAll((props.value as Props).props.map { Property(it.key, it::class.java, it.value) })
        return snapshot
    }

    override fun loadSnapshot(snapshot: ComponentSnapshot) {
        super.loadSnapshot(snapshot)
    }
}