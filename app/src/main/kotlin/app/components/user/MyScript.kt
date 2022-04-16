package app.components.user

import app.coroutines.Contexts
import app.eventbus.gui.EventBus
import app.eventbus.gui.UIEvent
import core.components.agent.AgentInterface
import core.components.base.Component
import core.components.base.Script
import core.entities.getComponent
import core.services.Services
import core.services.getAgentsToIdMap
import imgui.ImColor
import imgui.flag.ImGuiMouseButton
import imgui.internal.ImGui
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.launch

class MyScript : Component, Script {
    sealed class CellType {
        abstract class Agent(open val id: Int) : CellType()
        data class Bug(override val id: Int) : Agent(id)
        data class Ant(override val id: Int) : Agent(id)
        object Empty : CellType()
    }

    private val field = Array(20) { Array<CellType>(20) { CellType.Empty } }
    private var inspectedId: Int? = null

    init {
        CoroutineScope(Contexts.app).launch {
            EventBus.events.filterIsInstance<UIEvent.InspectAgent>().collectLatest {
                inspectedId = it.id
            }
        }
    }

    override fun onModelUpdate() {
        clearField()
        val agents = Services.scene.findAgentsThatHaving(Position::class)
        agents.forEach {agent ->
            val id = agent.getComponent<AgentInterface>()!!.id
            val position = agent.getComponent<Position>()!!
            val x = position.column
            val y = position.row
            field[y][x] = when (agent.agentType) {
                "Doodlebug" -> CellType.Bug(id)
                "Ant" -> CellType.Ant(id)
                else -> CellType.Empty
            }
        }
        getAgentsToIdMap().filter { (id, agent) -> agent.agentType == "Doodlebug" || agent.agentType == "Ant" }
            .forEach { (id, agent) ->

            }
    }

    override fun updateUI() {
        val drawList = ImGui.getWindowDrawList()
        val clipRectMin = drawList.clipRectMin
        val clipRectMax = drawList.clipRectMax
        val windowX = clipRectMin.x
        val windowY = clipRectMin.y
        val cellHeight = (clipRectMax.y - clipRectMin.y) / 20
        val cellWidth = (clipRectMax.x - clipRectMin.x) / 20
        repeat(20) { row ->
            repeat(20) { column ->
                val cellX = windowX + cellWidth * column
                val cellY = windowY + cellHeight * row
                val cell = field[row][column]
                when (cell) {
                    is CellType.Bug -> {
                        drawList.addRectFilled(
                            cellX,
                            cellY,
                            cellX + cellWidth,
                            cellY + cellHeight,
                            ImColor.intToColor(0, 255, 0)
                        )
                        inspectIfMoseClicked(cellX, cellY, cellWidth, cellHeight, cell.id)
                    }
                    is CellType.Ant -> {
                        drawList.addRectFilled(
                            cellX,
                            cellY,
                            cellX + cellWidth,
                            cellY + cellHeight,
                            ImColor.intToColor(255, 113, 113)
                        )
                        inspectIfMoseClicked(cellX, cellY, cellWidth, cellHeight, cell.id)
                    }
                    else -> {
                    }
                }
                drawList.addRect(
                    cellX,
                    cellY,
                    cellX + cellWidth,
                    cellY + cellHeight,
                    ImColor.intToColor(0, 0, 0)
                )

                inspectedId?.let {
                    if (cell is CellType.Agent && cell.id == inspectedId) {
                        drawList.addRectFilled(
                            cellX,
                            cellY,
                            cellX + cellWidth,
                            cellY + cellHeight,
                            ImColor.intToColor(0, 0, 0)
                        )
                    }
                }
            }
        }

    }

    private fun inspectIfMoseClicked(cellPosX: Float, cellPosY: Float, cellWidth: Float, cellHeight: Float, id: Int) {
        if (ImGui.isMouseClicked(ImGuiMouseButton.Left) && ImGui.isWindowFocused()) {
            val mousePos = ImGui.getMousePos()
            if (mousePos.x in cellPosX..(cellPosX + cellWidth) && mousePos.y in cellPosY..(cellPosY + cellHeight)) {
                CoroutineScope(Dispatchers.IO).launch {
                    EventBus.publish(UIEvent.InspectAgent(id))
                }
            }
        }
    }

    private fun clearField() {
        field.forEach { rows -> rows.indices.forEach { rows[it] = CellType.Empty } }
    }
}