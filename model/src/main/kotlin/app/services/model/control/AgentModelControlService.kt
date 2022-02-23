package app.services.model.control


import app.services.model.control.state.AgentModelControlContext
import core.api.AgentModelApiClient
import core.api.dto.State
import core.services.Service


class AgentModelControlService(apiClient: AgentModelApiClient) : Service() {
    private val context = AgentModelControlContext(apiClient)

    override fun start() = context.start()

    suspend fun connect(ip: String, port: Int): State = context.connect(ip, port)

    suspend fun run() = context.run()

    fun changeRequestPeriod(periodSec: Float) = context.changeRequestPeriod(periodSec)

    suspend fun pause() = context.pause()

    suspend fun resume() = context.resume()

    suspend fun stop() = context.stop()

    suspend fun disconnect() = context.disconnect()

}




