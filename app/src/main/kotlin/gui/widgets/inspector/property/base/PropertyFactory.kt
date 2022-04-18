package gui.widgets.inspector.property.base

import com.fasterxml.jackson.databind.JsonNode
import gui.widgets.properties.NodeTreeObjectProperty
import gui.widgets.properties.ObjectProperty
import gui.widgets.properties.Property

interface PropertyFactory<T : Property> {
    fun createProperty(
        name: String,
        value: Any,
        parentNode: JsonNode
    ): T

    fun createObjectProperty(
        name: String,
        parentNode: JsonNode
    ): ObjectProperty {
        return NodeTreeObjectProperty(name)
    }
}