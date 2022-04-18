package gui.widgets.properties

import core.utils.splitOnCapitalChars
import core.utils.uppercaseFirstChar
import imgui.ImGui
import gui.widgets.Widget

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