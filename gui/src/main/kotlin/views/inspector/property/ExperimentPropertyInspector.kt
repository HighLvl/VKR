package views.inspector.property

import com.fasterxml.jackson.databind.JsonNode
import views.inspector.property.base.PropertyInspector
import views.inspector.property.base.SimpleMutablePropertyFactory
import views.properties.MutableProperty
import views.properties.OpenFileProperty

class ExperimentPropertyInspector : PropertyInspector() {
    init {
        mutablePropertyFactory = MutablePropertyFactory()
    }

    private inner class MutablePropertyFactory : SimpleMutablePropertyFactory() {
        override fun createProperty(name: String, value: Any, parentNode: JsonNode): MutableProperty<*> {
            if (name == OPTIMIZATION_TASK_NAME && parentNode == node[MUTABLE_PROPS]) {
                return OpenFileProperty(name, value.toString(), LOAD_TASK_TITLE, TASK_FILE_EXTENSIONS_FILTER)
            }
            return super.createProperty(name, value, parentNode)
        }
    }

    private companion object {
        const val OPTIMIZATION_TASK_NAME = "task"
        const val LOAD_TASK_TITLE = "Load"
        const val TASK_FILE_EXTENSIONS_FILTER = "kts"
    }
}
