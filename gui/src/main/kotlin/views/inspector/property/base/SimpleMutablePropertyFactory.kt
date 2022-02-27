package views.inspector.property.base

import com.fasterxml.jackson.databind.JsonNode
import views.inspector.property.base.PropertyFactory
import views.properties.*

open class SimpleMutablePropertyFactory: PropertyFactory<MutableProperty<*>> {
    override fun createProperty(name: String, value: Any, parentNode: JsonNode): MutableProperty<*> {
        return when (value) {
            is Int -> IntMutableProperty(name, value)
            is Double -> DoubleMutableProperty(name, value)
            is Boolean -> BooleanMutableProperty(name, value)
            else -> StringMutableProperty(name, value.toString())
        }
    }
}