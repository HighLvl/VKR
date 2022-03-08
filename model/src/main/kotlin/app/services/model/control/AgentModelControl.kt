package app.services.model.control

import kotlinx.coroutines.flow.SharedFlow

interface AgentModelControl {
    val controlState: SharedFlow<ControlState>
    fun changeRequestPeriod(periodSec: Float)
    fun connect(ip: String, port: Int)
    fun runModel()
    fun pauseModel()
    fun resumeModel()
    fun stopModel()
    fun disconnect()
}