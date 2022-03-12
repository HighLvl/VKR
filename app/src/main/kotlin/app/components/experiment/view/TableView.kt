package app.components.experiment.view

import core.datatypes.base.Series
import imgui.ImGuiListClipper
import imgui.callback.ImListClipperCallback
import imgui.flag.ImGuiTableColumnFlags
import imgui.flag.ImGuiTableFlags
import imgui.internal.ImGui
import imgui.type.ImBoolean

open class TableView(private val windowTitle: String, private val dataSource: Map<String, Series<*>>) {
    private var docked = false
    protected val nameIndexMap = mutableMapOf<String, Int>()
    var enabled
        set(value) {
            showImBoolean.set(value)
        }
        get() = showImBoolean.get()
    private val showImBoolean = ImBoolean(false)

    open fun reset() {
        nameIndexMap.clear()
        dataSource.entries.forEachIndexed { index, (varName, _) ->
            nameIndexMap[varName] = index
        }
    }

    open fun update() {
        if (showImBoolean.get()) {
            show()
            if (!docked) {
                ImGui.dockBuilderDockWindow(windowTitle, ImGui.getWindowDockID())
                docked = true
            }
        }
    }

    private fun show() {
        if (ImGui.begin(windowTitle, showImBoolean)) {
            val columnsNumber = dataSource.keys.size
            if (columnsNumber > 0) {
                ImGui.beginTable(ID_TABLE, columnsNumber, ImGuiTableFlags.Borders or ImGuiTableFlags.Resizable)
                setupHeader()
                fillInTableWithData()
                ImGui.endTable()
            }
        }
        ImGui.end()
    }

    private fun setupHeader() {
        nameIndexMap.entries.forEach { (varName, index) ->
            ImGui.tableSetupColumn(
                varName,
                ImGuiTableColumnFlags.None,
                0f,
                index
            )
        }
        ImGui.tableHeadersRow()
    }

    protected open fun fillInTableWithData() {
        val rowsNumber = dataSource.values.first().size
        ImGuiListClipper.forEach(rowsNumber, object : ImListClipperCallback() {
            override fun accept(rowIndex: Int) {
                ImGui.tableNextRow()
                dataSource.entries.forEach { (varName, values) ->
                    val columnIndex = nameIndexMap[varName]!!
                    ImGui.tableSetColumnIndex(columnIndex)
                    drawCell(varName, rowIndex, values[rowIndex])
                }
            }
        })
    }

    protected open fun drawCell(columnTitle: String, row: Int, value: Any?) {
        ImGui.text(
            value?.toString() ?: ""
        )
    }

    private companion object {
        const val ID_TABLE = "table_id"
    }
}