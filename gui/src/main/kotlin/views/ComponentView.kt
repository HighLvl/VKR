package views

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import imgui.ImGui
import views.properties.*
import kotlin.reflect.KClass

class ComponentView : View {
    private lateinit var node: JsonNode
    private var propertyFactoryClass: KClass<out PropertyFactory> = ImmutablePropertyFactory::class

    fun inflate() {
        node = jacksonObjectMapper().readTree("{\"immutableProps\":[{\"name\":\"id\",\"type\":\"java.lang.Integer\",\"value\":1},{\"name\":\"props\",\"type\":\"java.util.LinkedHashMap\",\"value\":{\"a\":3,\"b\":7,\"text\":\"some text\",\"v\":1.3,\"list\":[1,{\"name\":\"id\",\"type\":\"java.lang.Integer\",\"value\":1},3]}}],\"mutableProps\":[{\"name\":\"a\",\"type\":\"java.lang.Integer\",\"value\":0},{\"name\":\"b\",\"type\":\"java.lang.Integer\",\"value\":45.6},{\"name\":\"c\",\"type\":\"java.lang.Integer\",\"value\":\"jljlj\"}]}"
        )

    }

    fun getJson() {
        node.get("immutableProps").get(0)
    }

    override fun draw() {
        ImGui.separator()
        ImGui.columns(2)
        useImmutablePropertyFactory()
        drawProps(node["immutableProps"])
        ImGui.separator()
        useMutablePropertyFactory()
        drawProps(node["mutableProps"])
    }

    private fun useImmutablePropertyFactory() {
        propertyFactoryClass = ImmutablePropertyFactory::class
    }

    private fun useMutablePropertyFactory() {
        propertyFactoryClass = MutablePropertyFactory::class
    }

    private fun drawProps(immutablePropsNode: JsonNode) {
        for (immutableProp in immutablePropsNode) {
            val name = immutableProp.get("name").asText()
            val valueNode = immutableProp.get("value")
            val property = buildProperty(name, valueNode, immutableProp, JsonNodeType.COMPONENT)
            property.draw()
        }
    }

    private fun buildProperty(
        name: String,
        valueNode: JsonNode,
        parentNode: JsonNode,
        parentNodeType: JsonNodeType
    ): Property {
        when {
            valueNode.isArray -> return buildObjectProperty(name, valueNode as ArrayNode)
            valueNode.isObject -> return buildObjectProperty(name, valueNode as ObjectNode)
        }
        val value = when {
            valueNode.isInt -> valueNode.asInt()
            valueNode.isDouble -> valueNode.asDouble()
            else -> valueNode.asText()
        }
        val propertyFactory = createPropertyFactory(name, value, parentNode, parentNodeType)
        return propertyFactory.createProperty()
    }

    private fun buildObjectProperty(
        name: String,
        arrayNode: ArrayNode
    ): ObjectProperty {
        val objectProperty = ObjectProperty(name)
        arrayNode.elements()
            .asSequence()
            .mapIndexed { index, jsonNode ->
                val propName = index.toString()
                buildProperty(propName, jsonNode, arrayNode, JsonNodeType.OBJECT)
            }
            .forEach {
                objectProperty.addProperty(it)
            }
        return objectProperty
    }

    private fun buildObjectProperty(
        name: String,
        objectNode: ObjectNode
    ): ObjectProperty {
        val objectProperty = ObjectProperty(name)
        objectNode.fields()
            .asSequence()
            .map { (propName, jsonNode) ->
                buildProperty(propName, jsonNode, objectNode, JsonNodeType.ARRAY)
            }
            .forEach {
                objectProperty.addProperty(it)
            }
        return objectProperty
    }

    private fun createPropertyFactory(
        name: String,
        value: Any,
        parentNode: JsonNode,
        parentNodeType: JsonNodeType
    ): PropertyFactory = when (propertyFactoryClass) {
        MutablePropertyFactory::class -> MutablePropertyFactory(name, value, parentNode, parentNodeType)
        ImmutablePropertyFactory::class -> ImmutablePropertyFactory(name, value, parentNode, parentNodeType)
        else -> throw IllegalStateException()
    }


    private enum class JsonNodeType {
        COMPONENT, ARRAY, OBJECT
    }

    private class MutablePropertyFactory(name: String, value: Any, parentNode: JsonNode, parentNodeType: JsonNodeType) :
        PropertyFactory(name, value, parentNode, parentNodeType) {

        override fun createProperty(): Property = when (value) {
            is Int -> IntMutableProperty(name) { newValue ->
                updatePropValue(newValue)
            }.apply {
                value = this@MutablePropertyFactory.value
            }

            is Double -> DoubleMutableProperty(name) { newValue ->
                updatePropValue(newValue)
            }.apply {
                value = this@MutablePropertyFactory.value
            }

            is String -> StringMutableProperty(name) { newValue ->
                updatePropValue(newValue)
            }.apply {
                value = this@MutablePropertyFactory.value
            }

            else -> throw IllegalStateException()
        }


        private fun updatePropValue(newValue: Any) {
            when (parentNodeType) {
                JsonNodeType.COMPONENT -> {
                    val parentObjectNode = parentNode as ObjectNode
                    parentObjectNode.put("value", newValue)
                }
                JsonNodeType.ARRAY -> {
                    val parentArrayNode = parentNode as ArrayNode
                    parentArrayNode.insert(name.toInt(), newValue)
                }
                JsonNodeType.OBJECT -> {
                    val parentObjectNode = parentNode as ObjectNode
                    parentObjectNode.put(name, newValue)
                }
            }
        }
    }

    private class ImmutablePropertyFactory(
        name: String,
        value: Any,
        parentNode: JsonNode,
        parentNodeType: JsonNodeType
    ) :
        PropertyFactory(name, value, parentNode, parentNodeType) {

        override fun createProperty(): Property {
            return StringImmutableProperty(name).apply {
                setString(value.toString())
            }
        }
    }

    private sealed class PropertyFactory(
        protected val name: String,
        protected val value: Any,
        protected val parentNode: JsonNode,
        protected val parentNodeType: JsonNodeType,
    ) {
        abstract fun createProperty(): Property
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

