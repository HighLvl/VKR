package widgets.properties

import app.utils.splitOnCapitalChars
import app.utils.uppercaseFirstChar
import imgui.ImGui
import widgets.Widget

abstract class Property(name: String) : Widget {
    protected val name = name.splitOnCapitalChars().uppercaseFirstChar()
    override fun draw() {
        ImGui.text("      $name")
        ImGui.nextColumn()
        drawValue()
        ImGui.nextColumn()
    }

    protected abstract fun drawValue()

}