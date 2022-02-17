package views.component

import imgui.ImGui
import imgui.flag.ImGuiTreeNodeFlags
import views.inspector.property.PropertyInspector

class ComponentView : PropertyInspector() {

    override fun draw() {
        val componentId = node["compClass"].asText()
        val componentName = getName(componentId)
        ImGui.pushID(componentId)
        if (ImGui.collapsingHeader(componentName, ImGuiTreeNodeFlags.DefaultOpen)) {
            super.draw()
        }
        ImGui.popID()
    }

    private fun getName(componentId: String) = componentId.split(".")
        .last()
        .replace("([^_])([A-Z])".toRegex(), "$1 $2")
}