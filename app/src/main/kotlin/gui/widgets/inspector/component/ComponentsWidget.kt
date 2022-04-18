package gui.widgets.inspector.component

import com.fasterxml.jackson.databind.node.ObjectNode
import gui.viewmodel.ComponentDto
import gui.widgets.Widget
import gui.widgets.component.CloseableComponentWidget
import gui.widgets.component.ComponentWidget
import gui.widgets.inspector.property.base.PropertyInspector

class ComponentsWidget(
    private val propertyInspectorFactory: PropertyInspectorFactory
) : Widget {
    var onCloseComponent: (Int) -> Unit = {}
    var components: List<ComponentDto> = listOf()
    var onChangePropertyListener: (Int, ObjectNode) -> Unit = { _, _ -> }

    override fun draw() {
        components.forEach { component ->
            val propertyInspector = propertyInspectorFactory.create(component)
            propertyInspector.setPropNode(component.properties)

            val componentView = when (component.removable) {
                false -> ComponentWidget(component.name, propertyInspector)
                else -> CloseableComponentWidget(component.name, propertyInspector) {
                    onCloseComponent(component.id)
                }
            }
            componentView.draw()
            if (propertyInspector.changed()) {
                onChangePropertyListener(component.id, propertyInspector.getDifference())
            }
        }
    }

    interface PropertyInspectorFactory {
        fun create(component: ComponentDto): PropertyInspector
    }
}