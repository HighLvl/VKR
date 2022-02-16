package views.component

import imgui.ImGui
import imgui.flag.ImGuiTreeNodeFlags
import views.View
import views.inspector.property.PropertyInspector

class Component(private val componentName: String) : View {
    private val propertyInspector = PropertyInspector()

    init {
        propertyInspector.inflate()
    }

    override fun draw() {
        ImGui.pushID(componentName + "1")
        if (ImGui.collapsingHeader(componentName, ImGuiTreeNodeFlags.DefaultOpen)) {
            propertyInspector.draw()
        }
        ImGui.popID()
    }
}