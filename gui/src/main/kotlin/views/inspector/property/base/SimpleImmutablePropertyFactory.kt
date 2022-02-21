package views.inspector.property.base

import com.fasterxml.jackson.databind.JsonNode
import views.properties.ImmutableProperty
import views.properties.StringImmutableProperty

open class SimpleImmutablePropertyFactory :
    PropertyFactory<ImmutableProperty<*>> {

    override fun createProperty(
        name: String,
        value: Any,
        parentNode: JsonNode): ImmutableProperty<*> {
        return StringImmutableProperty(name).apply {
            this.value = value.toString()
        }
    }
}