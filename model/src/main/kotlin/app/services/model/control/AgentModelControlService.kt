package app.services.model.control


import app.services.model.control.state.AgentModelControlContext
import core.api.AgentModelApiClient
import core.api.dto.State
import core.services.Service
import kotlinx.coroutines.flow.SharedFlow


class AgentModelControlService(apiClient: AgentModelApiClient) : Service() {
    private val context = AgentModelControlContext(apiClient)
    val controlState: SharedFlow<ControlState> = context.controlState

    override fun start() = context.start()
    fun changeRequestPeriod(periodSec: Float) = context.changeRequestPeriod(periodSec)
    suspend fun connect(ip: String, port: Int) = context.connect(ip, port)
    suspend fun run() = context.run()
    suspend fun pause() = context.pause()
    suspend fun resume() = context.resume()
    suspend fun stop() = context.stop()
    suspend fun disconnect() = context.disconnect()
}




