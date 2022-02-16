package views.inspector.property

import com.fasterxml.jackson.databind.JsonNode
import views.properties.DoubleMutableProperty
import views.properties.IntMutableProperty
import views.properties.Property
import views.properties.StringMutableProperty

class MutablePropertyFactory(private val onChangeValueListenerFactory: OnChangeValueListenerFactory) :
    PropertyBuilder.PropertyFactory() {
    override fun createProperty(
        name: String,
        value: Any,
        parentNode: JsonNode,
        parentNodeType: PropertyBuilder.JsonNodeType
    ): Property {
        val onChangeValueListener = onChangeValueListenerFactory.create(parentNodeType, parentNode, name)
        return when (value) {
            is Int -> IntMutableProperty(name, value, onChangeValueListener::onChangeValue)
            is Double -> DoubleMutableProperty(name, value, onChangeValueListener::onChangeValue)
            is String -> StringMutableProperty(name, value, onChangeValueListener::onChangeValue)
            else -> throw IllegalStateException()
        }
    }
}