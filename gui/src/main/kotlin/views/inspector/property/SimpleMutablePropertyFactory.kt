package views.inspector.property

import com.fasterxml.jackson.databind.JsonNode
import views.inspector.property.base.PropertyFactory
import views.properties.DoubleMutableProperty
import views.properties.IntMutableProperty
import views.properties.MutableProperty
import views.properties.StringMutableProperty

class SimpleMutablePropertyFactory: PropertyFactory<MutableProperty<*>> {
    override fun createProperty(name: String, value: Any, parentNode: JsonNode): MutableProperty<*> {
        return when (value) {
            is Int -> IntMutableProperty(name, value)
            is Double -> DoubleMutableProperty(name, value)
            is String -> StringMutableProperty(name, value)
            else -> throw IllegalStateException()
        }
    }
}