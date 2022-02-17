package views.inspector.property

import com.fasterxml.jackson.databind.JsonNode
import core.entities.Props
import views.properties.ListObjectProperty
import views.properties.ObjectProperty
import views.properties.Property
import views.properties.StringImmutableProperty

object ImmutablePropertyFactory :
    PropertyBuilder.PropertyFactory() {

    override fun createProperty(
        name: String,
        value: Any,
        parentNode: JsonNode,
        parentNodeType: PropertyBuilder.JsonNodeType
    ): Property {
        return StringImmutableProperty(name).apply {
            setString(value.toString())
        }
    }

    override fun createObjectProperty(
        name: String,
        parentNode: JsonNode,
        parentNodeType: PropertyBuilder.JsonNodeType
    ): ObjectProperty {
        if (parentNodeType == PropertyBuilder.JsonNodeType.COMPONENT &&
            parentNode["type"].asText() == Props::class.qualifiedName.toString()
        ) {
            return ListObjectProperty()
        }
        return super.createObjectProperty(name, parentNode, parentNodeType)
    }
}