package views.inspector.property

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.ObjectNode
import views.properties.ObjectProperty
import views.properties.Property

class PropertyBuilder(private val factory: PropertyFactory) {
    fun buildProperty(
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
        return factory.createProperty(name, value, parentNode, parentNodeType)
    }

    private fun buildObjectProperty(
        name: String,
        objectNode: ObjectNode
    ): ObjectProperty {
        val objectProperty = ObjectProperty(name)
        objectNode.fields()
            .asSequence()
            .map { (propName, jsonNode) ->
                buildProperty(propName, jsonNode, objectNode, JsonNodeType.OBJECT)
            }
            .forEach {
                objectProperty.addProperty(it)
            }
        return objectProperty
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
                buildProperty(propName, jsonNode, arrayNode, JsonNodeType.ARRAY)
            }
            .forEach {
                objectProperty.addProperty(it)
            }
        return objectProperty
    }

    abstract class PropertyFactory {
        abstract fun createProperty(
            name: String,
            value: Any,
            parentNode: JsonNode,
            parentNodeType: JsonNodeType
        ): Property
    }

    enum class JsonNodeType {
        COMPONENT, ARRAY, OBJECT
    }
}

