package widgets.inspector.property.base

import com.fasterxml.jackson.databind.JsonNode
import widgets.properties.NodeTreeObjectProperty
import widgets.properties.ObjectProperty
import widgets.properties.Property

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