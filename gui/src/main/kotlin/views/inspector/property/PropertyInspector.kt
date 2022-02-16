package views.inspector.property

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.JsonNodeFactory
import com.fasterxml.jackson.databind.node.ObjectNode
import imgui.ImGui
import views.View

open class PropertyInspector : View {
    private lateinit var node: JsonNode
    private val changedPropsNode = ObjectNode(JsonNodeFactory.instance)
    private val changedProps = changedPropsNode.putArray("mutableProps")


    fun inflate(node: JsonNode) {
        this.node = node
    }

    fun getJsonNode(): JsonNode {
        return changedPropsNode
    }

    fun changed() = !changedProps.isEmpty

    override fun draw() {
        changedProps.removeAll()
        ImGui.separator()
        ImGui.columns(2)
        drawImmutableProps()
        ImGui.separator()
        drawMutableProps()
        if (!changedProps.isEmpty) {
            println(changedProps)
        }
        ImGui.columns()
        repeat(2) { ImGui.spacing() }
    }

    private fun drawImmutableProps() {
        val immutablePropertyBuilder = PropertyBuilder(ImmutablePropertyFactory)
        for (immutableProp in node["immutableProps"]) {
            val name = immutableProp.get("name").asText()
            val valueNode = immutableProp.get("value")
            val property =
                immutablePropertyBuilder.buildProperty(
                    name, valueNode, immutableProp,
                    PropertyBuilder.JsonNodeType.COMPONENT
                )
            property.draw()
        }
    }

    private fun drawMutableProps() {
        for (prop in node["mutableProps"]) {
            val name = prop.get("name").asText()
            val valueNode = prop.get("value")
            val listenerFactory = OnChangeValueListenerFactoryImpl(prop, changedProps)
            val propertyFactory = MutablePropertyFactory(listenerFactory)
            val propertyBuilder = PropertyBuilder(propertyFactory)
            val property = propertyBuilder.buildProperty(name, valueNode, prop, PropertyBuilder.JsonNodeType.COMPONENT)
            property.draw()
        }
    }


    private fun drawProps(immutablePropsNode: JsonNode, propertyBuilder: PropertyBuilder) {
        for (immutableProp in immutablePropsNode) {
            val name = immutableProp.get("name").asText()
            val valueNode = immutableProp.get("value")
            val property = propertyBuilder.buildProperty(
                name, valueNode, immutableProp,
                PropertyBuilder.JsonNodeType.COMPONENT
            )
            property.draw()
        }
    }

}


