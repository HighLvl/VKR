package app.components.experiment.variables.observable

import app.components.experiment.fillInTableWithData
import core.datatypes.base.Series
import imgui.ImVec2
import imgui.extension.implot.ImPlot
import imgui.extension.implot.flag.ImPlotAxisFlags
import imgui.extension.implot.flag.ImPlotFlags
import imgui.flag.ImGuiMouseButton
import imgui.flag.ImGuiTableColumnFlags
import imgui.flag.ImGuiTableFlags
import imgui.internal.ImGui
import imgui.type.ImBoolean

class ObservableVariablesView(
    private val dataSource: Map<String, Series<Float>>
) {
    private val showChartImBooleans = mutableMapOf<String, ImBoolean>()
    private var docked = false
    private val chartWindowSizeInitialized = mutableMapOf<String, Boolean>()
    private val nameIndexMap = mutableMapOf<String, Int>()

    var enabled
        set(value) {
            showObservableVariablesImBoolean.set(value)
        }
        get() = showObservableVariablesImBoolean.get()
    private val showObservableVariablesImBoolean = ImBoolean(false)

    fun reset() {
        nameIndexMap.clear()
        showChartImBooleans.clear()
        chartWindowSizeInitialized.clear()
        dataSource.entries.forEachIndexed() { index, (varName, _) ->
            nameIndexMap[varName] = index
            showChartImBooleans[varName] = ImBoolean()
        }
    }

    fun update() {
        showOpenedObservableVariables()
        showChartImBooleans.entries.forEach { (varName, showed) ->
            showOpenedChart(showed, varName)
        }

    }

    private fun showOpenedObservableVariables() {
        if (showObservableVariablesImBoolean.get()) {
            showObservableVariables()
            if (!docked) {
                ImGui.dockBuilderDockWindow(TITLE_OBSERVABLE_VARIABLES_WINDOW, ImGui.getWindowDockID())
                docked = true
            }
        }
    }

    private fun showObservableVariables() {
        if (ImGui.begin(TITLE_OBSERVABLE_VARIABLES_WINDOW, showObservableVariablesImBoolean)) {
            val columnsNumber = dataSource.keys.size
            if (columnsNumber > 0) {
                ImGui.beginTable(ID_OBSERVABLE_VARIABLES_TABLE, columnsNumber, ImGuiTableFlags.Borders)
                setupObservableVarTableHeader()
                fillInTableWithData(dataSource, nameIndexMap)
                showObservableVarPopup()
                ImGui.endTable()
            }
        }
        ImGui.end()
    }

    private fun setupObservableVarTableHeader() {
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


    private fun showObservableVarPopup() {
        var hoveredColumn = -1
        nameIndexMap.entries.forEach { (varName, columnIndex) ->
            ImGui.pushID(columnIndex)
            if (ImGui.tableGetColumnFlags(columnIndex) and ImGuiTableColumnFlags.IsHovered != 0) {
                hoveredColumn = columnIndex
            }
            if (hoveredColumn == columnIndex && ImGui.isMouseReleased(ImGuiMouseButton.Right))
                ImGui.openPopup("MyPopup")
            if (ImGui.beginPopup("MyPopup")) {
                ImGui.checkbox("Show chart for $varName", showChartImBooleans[varName])
                ImGui.endPopup()
            }
            ImGui.popID()
        }
    }

    private fun showOpenedChart(showed: ImBoolean, varName: String) {
        if (showed.get()) {
            if (!chartWindowSizeInitialized.getOrDefault(varName, false)) {
                ImGui.setNextWindowSize(WIDTH_CHART_WINDOW, HEIGHT_CHART_WINDOW)
                chartWindowSizeInitialized[varName] = true
            }
            if (ImGui.begin("Chart $varName", showed)) {
                val windowSize = ImGui.getWindowSize()
                val plotSize = ImVec2(windowSize.x - DX_PLOT, windowSize.y - DY_PLOT)
                ImPlot.beginPlot(
                    varName,
                    "time",
                    "",
                    plotSize,
                    ImPlotFlags.None,
                    ImPlotAxisFlags.AutoFit,
                    ImPlotAxisFlags.AutoFit
                )
                ImPlot.plotLine(
                    varName,
                    dataSource["t"]!!.toList().toTypedArray(),
                    dataSource[varName]!!.toList().toTypedArray()
                )
                ImPlot.endPlot()
            }
            ImGui.end()
        }
    }

    private companion object {
        const val TITLE_OBSERVABLE_VARIABLES_WINDOW = "Observable Variables"
        const val ID_OBSERVABLE_VARIABLES_TABLE = "observable_table_id"
        const val DX_PLOT = 15f
        const val DY_PLOT = 50f
        const val WIDTH_CHART_WINDOW = 1000f
        const val HEIGHT_CHART_WINDOW = 500f
    }
}