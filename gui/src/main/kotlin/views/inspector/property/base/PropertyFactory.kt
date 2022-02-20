package views.inspector.property.base

import com.fasterxml.jackson.databind.JsonNode
import views.properties.NodeTreeObjectProperty
import views.properties.ObjectProperty
import views.properties.Property

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