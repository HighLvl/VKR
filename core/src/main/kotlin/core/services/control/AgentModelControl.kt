package core.services.control

interface AgentModelControl {
    val controlState: ControlState
    fun changeRequestPeriod(periodSec: Float)
    suspend fun connect(ip: String, port: Int)
    fun runModel(onResult: (Result<Unit>) -> Unit = {})
    fun pauseModel(onResult: (Result<Unit>) -> Unit = {})
    fun resumeModel(onResult: (Result<Unit>) -> Unit = {})
    fun stopModel(onResult: (Result<Unit>) -> Unit = {})
    suspend fun disconnect()
}