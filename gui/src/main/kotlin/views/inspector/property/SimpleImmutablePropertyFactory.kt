package views.inspector.property

import com.fasterxml.jackson.databind.JsonNode
import views.inspector.property.base.PropertyFactory
import views.properties.ImmutableProperty
import views.properties.Property
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