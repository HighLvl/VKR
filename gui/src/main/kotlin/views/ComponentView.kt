package views

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.JsonNodeFactory
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import imgui.ImGui
import views.properties.*

class ComponentView : View {
    private lateinit var node: JsonNode
    private val changedProps = run{
        ObjectNode(JsonNodeFactory.instance).putArray("mutableProps")
    }

    fun inflate() {
        node = jacksonObjectMapper().readTree("{\"immutableProps\":[{\"name\":\"id\",\"type\":\"java.lang.Integer\",\"value\":1},{\"name\":\"props\",\"type\":\"java.util.LinkedHashMap\",\"value\":{\"a\":3,\"b\":7,\"text\":\"some text\",\"v\":1.3,\"list\":[1,{\"name\":\"id\",\"type\":\"java.lang.Integer\",\"value\":1},3]}}],\"mutableProps\":[{\"name\":\"a\",\"type\":\"java.lang.Integer\",\"value\":0},{\"name\":\"b\",\"type\":\"java.lang.Integer\",\"value\":45.6},{\"name\":\"c\",\"type\":\"java.lang.Integer\",\"value\":\"jljlj\"},{\"name\":\"id\",\"type\":\"java.lang.Integer\",\"value\":1},{\"name\":\"props\",\"type\":\"java.util.LinkedHashMap\",\"value\":{\"a\":3,\"b\":7,\"text\":\"some text\",\"v\":1.3,\"list\":[1,{\"name\":\"id\",\"type\":\"java.lang.Integer\",\"value\":1},3]}}]}"
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
    }

    private fun drawImmutableProps() {
        val immutablePropertyBuilder = PropertyBuilder(ImmutablePropertyFactory)
        for (immutableProp in node["immutableProps"]) {
            val name = immutableProp.get("name").asText()
            val valueNode = immutableProp.get("value")
            val property = immutablePropertyBuilder.buildProperty(name, valueNode, immutableProp, JsonNodeType.COMPONENT)
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
            val property = propertyBuilder.buildProperty(name, valueNode, prop, JsonNodeType.COMPONENT)
            property.draw()
        }
    }


    private fun drawProps(immutablePropsNode: JsonNode, propertyBuilder: PropertyBuilder) {
        for (immutableProp in immutablePropsNode) {
            val name = immutableProp.get("name").asText()
            val valueNode = immutableProp.get("value")
            val property = propertyBuilder.buildProperty(name, valueNode, immutableProp, JsonNodeType.COMPONENT)
            property.draw()
        }
    }

    private class OnChangeValueListenerFactoryImpl(private val componentNode: JsonNode, private val changedNodeProps: ArrayNode) : OnChangeValueListenerFactory {
        override fun create(
            parentNodeType: JsonNodeType,
            parentNode: JsonNode,
            propName: String
        ): OnChangeValueListener {
            return OnChangeValueListenerImpl(parentNodeType, parentNode, componentNode, propName, changedNodeProps)
        }
    }

    private class OnChangeValueListenerImpl(
        private val parentNodeType: JsonNodeType,
        private val parentNode: JsonNode,
        private val componentNode: JsonNode,
        private val propName: String,
        private val changedNodeProps: ArrayNode
    ) :
        OnChangeValueListener {
        override fun onChangeValue(newValue: Any) {
            updatePropValue(newValue)
        }

        private fun updatePropValue(newValue: Any) {
            when (parentNodeType) {
                JsonNodeType.COMPONENT -> {
                    val parentObjectNode = parentNode as ObjectNode
                    parentObjectNode.put("value", newValue)
                    changedNodeProps.add(componentNode)
                }
                JsonNodeType.ARRAY -> {
                    val parentArrayNode = parentNode as ArrayNode
                    val index = propName.toInt()
                    parentArrayNode.remove(index)
                    parentArrayNode.insert(index, newValue)
                    changedNodeProps.add(componentNode)

                }
                JsonNodeType.OBJECT -> {
                    val parentObjectNode = parentNode as ObjectNode
                    parentObjectNode.put(propName, newValue)
                    changedNodeProps.add(componentNode)

                }
            }
        }
    }

    private interface OnChangeValueListener {
        fun onChangeValue(newValue: Any)
    }

    private interface OnChangeValueListenerFactory {
        fun create(
            parentNodeType: JsonNodeType,
            parentNode: JsonNode,
            propName: String
        ): OnChangeValueListener
    }

    private class MutablePropertyFactory(private val onChangeValueListenerFactory: OnChangeValueListenerFactory) :
        PropertyFactory() {
        override fun createProperty(
            name: String,
            value: Any,
            parentNode: JsonNode,
            parentNodeType: JsonNodeType
        ): Property {
            val onChangeValueListener = onChangeValueListenerFactory.create(parentNodeType, parentNode, name)
            return when (value) {
                is Int -> IntMutableProperty(name, value, onChangeValueListener::onChangeValue)
                is Double -> DoubleMutableProperty(name, value, onChangeValueListener::onChangeValue)
                is String -> StringMutableProperty(name, value, onChangeValueListener::onChangeValue)
                else -> throw IllegalStateException()
            }
        }
    }

    private object ImmutablePropertyFactory :
        PropertyFactory() {

        override fun createProperty(
            name: String,
            value: Any,
            parentNode: JsonNode,
            parentNodeType: JsonNodeType
        ): Property {
            return StringImmutableProperty(name).apply {
                setString(value.toString())
            }
        }
    }


}


private fun ObjectNode.put(name: String, value: Any) {
    when (value) {
        is Int -> put(name, value)
        is Double -> put(name, value)
        is String -> put(name, value)
        else -> {
        }
    }
}

private fun ArrayNode.insert(index: Int, value: Any) {
    when (value) {
        is Int -> insert(index, value)
        is Double -> insert(index, value)
        is String -> insert(index, value)
        else -> {
        }
    }
}

