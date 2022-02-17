package views.inspector.property

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.ObjectNode
import views.properties.NodeTreeObjectProperty
import views.properties.ObjectProperty
import views.properties.Property

class PropertyBuilder(private val factory: PropertyFactory) {
    private lateinit var parentNode: JsonNode
    private lateinit var parentNodeType: JsonNodeType

    fun buildProperty(
        name: String,
        valueNode: JsonNode,
        parentNode: JsonNode,
        parentNodeType: JsonNodeType
    ): Property {
        this.parentNode = parentNode
        this.parentNodeType = parentNodeType
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
        val objectProperty = factory.createObjectProperty(name, parentNode, parentNodeType) // TODO move create object property to factory
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
        val objectProperty = factory.createObjectProperty(name, parentNode, parentNodeType)
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

        open fun createObjectProperty(
            name: String,
            parentNode: JsonNode,
            parentNodeType: JsonNodeType
        ): ObjectProperty {
            return NodeTreeObjectProperty(name)
        }
    }

    enum class JsonNodeType {
        COMPONENT, ARRAY, OBJECT
    }
}

