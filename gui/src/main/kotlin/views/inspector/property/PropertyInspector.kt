package views.inspector.property

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.JsonNodeFactory
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import imgui.ImGui
import views.View

class PropertyInspector : View {
    private lateinit var node: JsonNode
    private val changedProps = run {
        ObjectNode(JsonNodeFactory.instance).putArray("mutableProps")
    }

    fun inflate() {
        node = jacksonObjectMapper().readTree(
            "{\"immutableProps\":[{\"name\":\"id\",\"type\":\"java.lang.Integer\",\"value\":1},{\"name\":\"props\",\"type\":\"java.util.LinkedHashMap\",\"value\":{\"a\":3,\"b\":7,\"text\":\"some text\",\"v\":1.3,\"list\":[1,{\"name\":\"id\",\"type\":\"java.lang.Integer\",\"value\":1},3]}}],\"mutableProps\":[{\"name\":\"a\",\"type\":\"java.lang.Integer\",\"value\":0},{\"name\":\"b\",\"type\":\"java.lang.Integer\",\"value\":45.6},{\"name\":\"c\",\"type\":\"java.lang.Integer\",\"value\":\"jljlj\"},{\"name\":\"id\",\"type\":\"java.lang.Integer\",\"value\":1},{\"name\":\"props\",\"type\":\"java.util.LinkedHashMap\",\"value\":{\"a\":3,\"b\":7,\"text\":\"some text\",\"v\":1.3,\"list\":[1,{\"name\":\"id\",\"type\":\"java.lang.Integer\",\"value\":1},3]}}]}"
        )
    }

    fun getJson() {
        node.get("immutableProps").get(0)
    }

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


