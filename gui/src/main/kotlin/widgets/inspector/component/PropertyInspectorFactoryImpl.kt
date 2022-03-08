package widgets.inspector.component

import com.fasterxml.jackson.databind.node.ObjectNode
import viewmodel.AgentInterface
import viewmodel.ComponentDto
import viewmodel.Experiment
import widgets.inspector.property.AgentInterfacePropertyInspector
import widgets.inspector.property.ExperimentPropertyInspector
import widgets.inspector.property.base.PropertyInspector

class PropertyInspectorFactoryImpl(
    private val onChangeValueListener: (String, ObjectNode) -> Unit,
    private val onCommitListener: (String) -> Unit
) : ComponentsWidget.PropertyInspectorFactory {
    override fun create(component: ComponentDto): PropertyInspector = when (component) {
        is AgentInterface -> AgentInterfacePropertyInspector(component.requestBodies).apply {
            onChangeValueListener = this@PropertyInspectorFactoryImpl.onChangeValueListener
            onCommitListener = this@PropertyInspectorFactoryImpl.onCommitListener
        }
        is Experiment -> ExperimentPropertyInspector()
        else -> PropertyInspector()
    }
}