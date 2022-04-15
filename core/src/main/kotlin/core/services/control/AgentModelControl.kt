package core.services.control

import kotlinx.coroutines.flow.SharedFlow

interface AgentModelControl {
    val controlState: SharedFlow<ControlState>
    fun changeRequestPeriod(periodSec: Float)
    suspend fun connect(ip: String, port: Int)
    fun runModel(onResult: (Result<Unit>) -> Unit = {})
    fun pauseModel(onResult: (Result<Unit>) -> Unit = {})
    fun resumeModel(onResult: (Result<Unit>) -> Unit = {})
    fun stopModel(onResult: (Result<Unit>) -> Unit = {})
    fun disconnect()
}