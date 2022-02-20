package views.inspector.property

import com.fasterxml.jackson.databind.JsonNode
import views.inspector.property.base.PropertyBuilder
import views.properties.Property
import views.properties.StringImmutableProperty

open class SimpleImmutablePropertyFactory :
    PropertyBuilder.PropertyFactory() {

    override fun createProperty(
        name: String,
        value: Any,
        parentNode: JsonNode): Property {
        return StringImmutableProperty(name).apply {
            setString(value.toString())
        }
    }
}