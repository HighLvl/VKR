package app.components.experiment.variables.observable

import app.components.experiment.view.TableView
import core.datatypes.base.Series
import imgui.ImVec2
import imgui.extension.implot.ImPlot
import imgui.extension.implot.flag.ImPlotAxisFlags
import imgui.extension.implot.flag.ImPlotFlags
import imgui.flag.ImGuiMouseButton
import imgui.flag.ImGuiTableColumnFlags
import imgui.internal.ImGui
import imgui.type.ImBoolean

class ObservableVariablesView(
    private val dataSource: Map<String, Series<Float>>
) : TableView(TITLE_OBSERVABLE_VARIABLES_WINDOW, dataSource) {
    private val showChartImBooleans = mutableMapOf<String, ImBoolean>()
    private val chartWindowSizeInitialized = mutableMapOf<String, Boolean>()

    override fun reset() {
        super.reset()
        showChartImBooleans.clear()
        dataSource.keys.forEach { varName ->
            showChartImBooleans[varName] = ImBoolean()
        }
        chartWindowSizeInitialized.clear()
    }

    override fun update() {
        super.update()
        showChartImBooleans.entries.forEach { (varName, showed) ->
            showOpenedChart(showed, varName)
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


    override fun fillInTableWithData() {
        super.fillInTableWithData()
        showObservableVarPopup()
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

    private companion object {
        const val TITLE_OBSERVABLE_VARIABLES_WINDOW = "Observable Variables"
        const val DX_PLOT = 15f
        const val DY_PLOT = 50f
        const val WIDTH_CHART_WINDOW = 1000f
        const val HEIGHT_CHART_WINDOW = 500f
    }
}