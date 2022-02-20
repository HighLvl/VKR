package views.inspector.property.base

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.JsonNodeFactory
import com.fasterxml.jackson.databind.node.ObjectNode
import imgui.ImGui
import views.View
import views.inspector.property.OnChangeValueListenerFactoryImpl
import views.inspector.property.SimpleImmutablePropertyFactory
import views.inspector.property.SimpleMutablePropertyFactory
import views.properties.ImmutableProperty
import views.properties.MutableProperty

open class PropertyInspector(
    protected var immutablePropertyFactory: PropertyFactory<ImmutableProperty<*>> = SimpleImmutablePropertyFactory(),
    protected var mutablePropertyFactory: PropertyFactory<MutableProperty<*>> = SimpleMutablePropertyFactory()
) : View {

    protected lateinit var node: JsonNode
    private val changedPropsNode = ObjectNode(JsonNodeFactory.instance)
    private val changedProps = changedPropsNode.putArray(MUTABLE_PROPS)

    fun setPropNode(node: JsonNode) {
        this.node = node
    }

    fun getDifference(): ObjectNode {
        return changedPropsNode
    }

    fun changed() = !changedProps.isEmpty

    override fun draw() {
        changedProps.removeAll()
        ImGui.separator()
        ImGui.columns(2)
        drawProperties()
        ImGui.columns()
        repeat(2) { ImGui.spacing() }
    }

    protected open fun drawProperties() {
        drawImmutableProps()
        ImGui.separator()
        drawMutableProps()
        if (!changedProps.isEmpty) {
            println(changedProps)
        }
    }

    private fun drawImmutableProps() {
        val immutablePropertyBuilder = PropertyBuilder(immutablePropertyFactory)
        val parentNode = node[IMMUTABLE_PROPS]
        parentNode.fields().forEach { (propName, valueNode) ->
            val property =
                immutablePropertyBuilder.buildProperty(
                    propName, valueNode, parentNode)
            property.draw()
        }
    }

    private fun drawMutableProps() {
        val parentNode = node[MUTABLE_PROPS]
        parentNode.fields().forEach { (propName, valueNode) ->
            val listenerFactory = OnChangeValueListenerFactoryImpl(parentNode, changedProps)
            val propertyFactory = MutablePropertyFactory(mutablePropertyFactory, listenerFactory)
            val propertyBuilder = PropertyBuilder(propertyFactory)
            val property = propertyBuilder.buildProperty(propName, valueNode, parentNode)
            property.draw()
        }
    }

    protected companion object {
        const val IMMUTABLE_PROPS = "immutableProps"
        const val MUTABLE_PROPS = "mutableProps"
    }
}


