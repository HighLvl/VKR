package widgets.inspector.property.base

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.ObjectNode
import widgets.properties.ObjectProperty
import widgets.properties.Property

class PropertyBuilder(private val factory: PropertyFactory<*>) {
    private lateinit var parentNode: JsonNode

    fun buildProperty(
        name: String,
        valueNode: JsonNode,
        parentNode: JsonNode
    ): Property {
        this.parentNode = parentNode
        when {
            valueNode.isArray -> return buildObjectProperty(name, valueNode as ArrayNode)
            valueNode.isObject -> return buildObjectProperty(name, valueNode as ObjectNode)
        }
        val value = when {
            valueNode.isInt -> valueNode.asInt()
            valueNode.isLong -> valueNode.asLong()
            valueNode.isBoolean -> valueNode.asBoolean()
            valueNode.isDouble || valueNode.isFloat -> valueNode.asDouble()
            else -> valueNode.asText()
        }
        return factory.createProperty(name, value, parentNode)
    }

    private fun buildObjectProperty(
        name: String,
        objectNode: ObjectNode
    ): ObjectProperty {
        val objectProperty = factory.createObjectProperty(
            name,
            parentNode
        )
        objectNode.fields()
            .asSequence()
            .map { (propName, jsonNode) ->
                buildProperty(propName, jsonNode, objectNode)
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
        val objectProperty = factory.createObjectProperty(name, parentNode)
        arrayNode.elements()
            .asSequence()
            .mapIndexed { index, jsonNode ->
                val propName = index.toString()
                buildProperty(propName, jsonNode, arrayNode)
            }
            .forEach {
                objectProperty.addProperty(it)
            }
        return objectProperty
    }

}

