package app.services.model.control


import app.services.user.AgentModelControl
import app.services.model.control.state.AgentModelControlContext
import core.api.AgentModelApiClient
import core.services.Service
import kotlinx.coroutines.flow.SharedFlow

class AgentModelControlService(apiClient: AgentModelApiClient) : Service(), AgentModelControl {
    private val context = AgentModelControlContext(apiClient)
    val controlState: SharedFlow<ControlState> = context.controlState

    override fun start() {
        super.start()
        context.start()
    }

    override fun stop() {
        super.stop()
        context.stop()
    }

    override fun changeRequestPeriod(periodSec: Float) = context.changeRequestPeriod(periodSec)
    suspend fun connect(ip: String, port: Int) = context.connect(ip, port)
    override suspend fun runModel() = context.runModel()
    override suspend fun pauseModel() = context.pauseModel()
    override suspend fun resumeModel() = context.resumeModel()
    override suspend fun stopModel() = context.stopModel()
    suspend fun disconnect() = context.disconnect()
}




