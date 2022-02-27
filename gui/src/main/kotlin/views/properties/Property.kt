package views.properties

import imgui.ImGui
import views.View
import views.splitOnCapitalLetters

abstract class Property(protected val name: String) : View {
    override fun draw() {
        ImGui.text("      ${name.splitOnCapitalLetters()}")
        ImGui.nextColumn()
        drawValue()
        ImGui.nextColumn()
    }

    protected abstract fun drawValue()

}