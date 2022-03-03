package views.inspector.component

import app.components.AgentInterface
import app.components.experiment.Experiment
import core.components.Component
import views.inspector.property.AgentInterfacePropertyInspector
import views.inspector.property.ExperimentPropertyInspector
import views.inspector.property.base.PropertyInspector

class PropertyInspectorFactoryImpl : ComponentInspector.PropertyInspectorFactory {
    override fun create(component: Component): PropertyInspector = when (component) {
        is AgentInterface -> AgentInterfacePropertyInspector(component.requestBodies)
        is Experiment -> ExperimentPropertyInspector()
        else -> PropertyInspector()
    }
}