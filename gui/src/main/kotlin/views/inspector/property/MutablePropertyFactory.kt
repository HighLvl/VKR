package views.inspector.property

import com.fasterxml.jackson.databind.JsonNode
import views.inspector.property.base.OnChangeValueListenerFactory
import views.inspector.property.base.PropertyBuilder
import views.properties.*

open class MutablePropertyFactory(private val onChangeValueListenerFactory: OnChangeValueListenerFactory) :
    PropertyBuilder.PropertyFactory() {
    override fun createProperty(
        name: String,
        value: Any,
        parentNode: JsonNode
    ): Property {
        val onChangeValueListener = onChangeValueListenerFactory.create(parentNode, name)
        return when (value) {
            is Int -> IntMutableProperty(name, value, onChangeValueListener::onChangeValue)
            is Double -> DoubleMutableProperty(name, value, onChangeValueListener::onChangeValue)
            is String -> StringMutableProperty(name, value, onChangeValueListener::onChangeValue)
            else -> throw IllegalStateException()
        }
    }


}