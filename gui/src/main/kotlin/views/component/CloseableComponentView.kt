package views.component

import imgui.ImGui
import imgui.flag.ImGuiTreeNodeFlags
import imgui.type.ImBoolean
import views.inspector.property.base.PropertyInspector

class CloseableComponentView(
    private val compClass: String,
    propertyInspector: PropertyInspector,
    private val onClickCloseButton: () -> Unit
) : ComponentView(
    compClass,
    propertyInspector
) {
    private val isVisible = ImBoolean(true)
    override fun draw() {
        val componentId = compClass
        val componentName = getName(componentId)
        ImGui.pushID(componentId)
        val flags = ImGuiTreeNodeFlags.DefaultOpen
        if (ImGui.collapsingHeader(componentName, isVisible, flags)) {
            drawPropertyInspector()
            if (!isVisible.get()) onClickCloseButton()
        }
        ImGui.popID()
    }
}