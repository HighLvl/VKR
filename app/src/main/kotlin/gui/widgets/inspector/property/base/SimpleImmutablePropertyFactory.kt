package gui.widgets.inspector.property.base

import com.fasterxml.jackson.databind.JsonNode
import gui.widgets.properties.ImmutableProperty
import gui.widgets.properties.StringImmutableProperty

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