package gui.widgets.inspector.property

import com.fasterxml.jackson.databind.JsonNode
import gui.utils.getString
import gui.widgets.inspector.property.base.PropertyInspector
import gui.widgets.inspector.property.base.SimpleMutablePropertyFactory
import gui.widgets.properties.MutableProperty
import gui.widgets.properties.OpenFileProperty

class ConfigurationPropertyInspector: PropertyInspector() {
    init {
        mutablePropertyFactory = MutablePropertyFactory()
    }

    private inner class MutablePropertyFactory : SimpleMutablePropertyFactory() {
        override fun createProperty(name: String, value: Any, parentNode: JsonNode): MutableProperty<*> {
            if (name == MODEL_CONFIGURATION_NAME && parentNode == node[MUTABLE_PROPS]) {
                return OpenFileProperty(name, value.toString(), LOAD_TITLE, TASK_FILE_EXTENSIONS_FILTER)
            }
            return super.createProperty(name, value, parentNode)
        }
    }

    private companion object {
        const val MODEL_CONFIGURATION_NAME = "modelConfiguration"
        val LOAD_TITLE = getString("model_configuration_load_title")
        const val TASK_FILE_EXTENSIONS_FILTER = "kts"
    }
}