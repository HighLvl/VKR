package gui.widgets.inspector.component

import com.fasterxml.jackson.databind.node.ObjectNode
import gui.viewmodel.AgentInterface
import gui.viewmodel.ComponentDto
import gui.viewmodel.Configuration
import gui.viewmodel.Experiment
import gui.widgets.inspector.property.AgentInterfacePropertyInspector
import gui.widgets.inspector.property.ConfigurationPropertyInspector
import gui.widgets.inspector.property.ExperimentPropertyInspector
import gui.widgets.inspector.property.base.PropertyInspector

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
        is Configuration -> ConfigurationPropertyInspector()
        else -> PropertyInspector()
    }
}