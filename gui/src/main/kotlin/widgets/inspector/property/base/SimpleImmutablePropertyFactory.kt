package widgets.inspector.property.base

import com.fasterxml.jackson.databind.JsonNode
import widgets.properties.ImmutableProperty
import widgets.properties.StringImmutableProperty

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