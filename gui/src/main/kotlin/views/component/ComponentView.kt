package views.component

import imgui.ImGui
import imgui.flag.ImGuiTreeNodeFlags
import views.View
import views.inspector.property.PropertyInspector

class ComponentView(private val componentName: String) : PropertyInspector() {
    override fun draw() {
        ImGui.pushID(componentName + "1")
        if (ImGui.collapsingHeader(componentName, ImGuiTreeNodeFlags.DefaultOpen)) {
            super.draw()
        }
        ImGui.popID()
    }
}