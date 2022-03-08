package widgets.component

import imgui.ImGui
import imgui.flag.ImGuiTreeNodeFlags
import imgui.type.ImBoolean
import widgets.inspector.property.base.PropertyInspector

class CloseableComponentWidget(
    private val compClass: String,
    propertyInspector: PropertyInspector,
    private val onClickCloseButton: () -> Unit
) : ComponentWidget(
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