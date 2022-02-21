package views.inspector.property.base

import app.logger.Log
import app.logger.Logger
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.JsonNodeFactory
import com.fasterxml.jackson.databind.node.ObjectNode
import imgui.ImGui
import views.View
import views.properties.ImmutableProperty
import views.properties.MutableProperty

open class PropertyInspector(
    protected var immutablePropertyFactory: PropertyFactory<ImmutableProperty<*>> = SimpleImmutablePropertyFactory(),
    protected var mutablePropertyFactory: PropertyFactory<MutableProperty<*>> = SimpleMutablePropertyFactory()
) : View {

    protected lateinit var node: JsonNode
    private val changedPropsNode = ObjectNode(JsonNodeFactory.instance)
    private val changedProps = changedPropsNode.putObject(MUTABLE_PROPS)

    fun setPropNode(node: JsonNode) {
        this.node = node
    }

    fun getDifference(): ObjectNode {
        return changedPropsNode
    }

    fun changed() = !changedProps.isEmpty

    override fun draw() {
        changedProps.removeAll()
        ImGui.columns(2)
        drawProperties()
        ImGui.columns()
        repeat(2) { ImGui.spacing() }
    }

    protected open fun drawProperties() {
        drawImmutableProps()
        drawMutableProps()
        if (!changedProps.isEmpty) {
            Logger.log(changedProps.toString(), Log.Level.DEBUG)
        }
    }

    private fun drawImmutableProps() {
        val parentNode = node[IMMUTABLE_PROPS]
        if (!parentNode.isEmpty) {
            ImGui.separator()
        }
        val immutablePropertyBuilder = PropertyBuilder(immutablePropertyFactory)
        parentNode.fields().forEach { (propName, valueNode) ->
            val property =
                immutablePropertyBuilder.buildProperty(
                    propName, valueNode, parentNode)
            property.draw()
        }
    }

    private fun drawMutableProps() {
        val parentNode = node[MUTABLE_PROPS]
        if (!parentNode.isEmpty) {
            ImGui.separator()
        }
        parentNode.fields().forEach { (propName, valueNode) ->
            val listenerFactory = OnChangeValueListenerFactoryImpl(propName to parentNode, changedProps)
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


