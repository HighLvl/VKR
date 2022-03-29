package app.components.experiment.view

import core.datatypes.base.Series
import core.datatypes.seriesOf
import imgui.ImGuiListClipper
import imgui.callback.ImListClipperCallback
import imgui.flag.ImGuiTableColumnFlags
import imgui.flag.ImGuiTableFlags
import imgui.internal.ImGui
import imgui.type.ImBoolean

open class TableView(
    private val windowTitle: String,
    private val dataSource: Map<String, Series<*>>,
    private val rowTypes: Series<Int> = seriesOf()
) {
    private var docked = false
    protected val indexNameMap = mutableMapOf<Int, String>()
    var enabled
        set(value) {
            showImBoolean.set(value)
        }
        get() = showImBoolean.get()
    private val showImBoolean = ImBoolean(false)

    protected val columnNumber
        get() = dataSource.keys.size

    open fun reset() {
        indexNameMap.clear()
        dataSource.entries.forEachIndexed { index, (varName, _) ->
            indexNameMap[index] = varName
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
            if (columnNumber > 0) {
                ImGui.beginTable(ID_TABLE, columnNumber, ImGuiTableFlags.Borders or ImGuiTableFlags.Resizable)
                setupHeader()
                fillInTableWithData()
                ImGui.endTable()
            }
        }
        ImGui.end()
    }

    open fun formatTitle(title: String, columnIndex: Int): String {
        return title
    }

    private fun setupHeader() {
        indexNameMap.entries.forEach { (index, varName) ->
            ImGui.tableSetupColumn(
                formatTitle(varName, index),
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
                dataSource.entries.forEachIndexed { index, (_, values) ->
                    ImGui.tableSetColumnIndex(index)
                    drawCell(
                        index,
                        rowIndex,
                        values[rowIndex],
                        if (rowTypes.size == 0) 0 else rowTypes[rowIndex] ?: 0
                    )
                }
            }
        })
    }

    protected open fun drawCell(column: Int, row: Int, value: Any?, rowType: Int) {
        ImGui.text(
            value?.toString() ?: ""
        )
    }

    private companion object {
        const val ID_TABLE = "table_id"
    }
}