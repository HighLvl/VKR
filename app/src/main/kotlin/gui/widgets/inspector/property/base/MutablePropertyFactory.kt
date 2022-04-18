package gui.widgets.inspector.property.base

import com.fasterxml.jackson.databind.JsonNode
import gui.widgets.properties.*

open class MutablePropertyFactory(
    private val mutablePropertyFactory: PropertyFactory<MutableProperty<*>>,
    private val onChangeValueListenerFactory: OnChangeValueListenerFactory
) : PropertyFactory<MutableProperty<*>> by mutablePropertyFactory {
    override fun createProperty(
        name: String,
        value: Any,
        parentNode: JsonNode
    ): MutableProperty<*> {
        val onChangeValueListener = onChangeValueListenerFactory.create(parentNode, name)
        return mutablePropertyFactory.createProperty(name, value, parentNode)
            .apply { onChangeValue = onChangeValueListener::onChangeValue }
    }


}