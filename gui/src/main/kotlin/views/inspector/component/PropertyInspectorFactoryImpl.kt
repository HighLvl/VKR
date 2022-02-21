package views.inspector.component

import app.components.AgentInterface
import app.components.optimization.OptimizationTask
import core.components.base.Component
import views.inspector.property.AgentInterfacePropertyInspector
import views.inspector.property.OptimizationTaskPropertyInspector
import views.inspector.property.base.PropertyInspector

class PropertyInspectorFactoryImpl : ComponentInspector.PropertyInspectorFactory {
    override fun create(component: Component): PropertyInspector = when (component) {
        is AgentInterface -> AgentInterfacePropertyInspector(component.requestBodies)
        is OptimizationTask -> OptimizationTaskPropertyInspector()
        else -> PropertyInspector()
    }
}