package core.services.control

interface AgentModelControl {
    val controlState: ControlState
    fun changeRequestPeriod(periodSec: Float)
    suspend fun connect(ip: String, port: Int, onResult: suspend (Result<Unit>) -> Unit = {})
    fun runModel(onResult: suspend (Result<Unit>) -> Unit = {})
    fun pauseModel(onResult: suspend (Result<Unit>) -> Unit = {})
    fun resumeModel(onResult: suspend (Result<Unit>) -> Unit = {})
    fun stopModel(onResult: suspend (Result<Unit>) -> Unit = {})
    suspend fun disconnect()
}