package app.services.model.control


import app.services.Service
import app.services.model.control.state.AgentModelControlContext
import app.services.scene.SceneApi
import app.api.base.AgentModelApi
import app.requests.RequestDispatcher
import app.requests.RequestSender
import core.services.control.AgentModelControl
import core.services.control.ControlState
import kotlinx.coroutines.flow.SharedFlow

class AgentModelControlService(modelApi: AgentModelApi, sceneApi: SceneApi, requestDispatcher: RequestDispatcher, requestSender: RequestSender) : Service(), AgentModelControl {
    private val context = AgentModelControlContext(modelApi, sceneApi, requestDispatcher, requestSender)
    val controlStateFlow: SharedFlow<ControlState> = context.controlStateFlow
    override val controlState: ControlState
        get() = context.controlState

    override fun start() {
        super.start()
        context.start()
    }

    override fun stop() {
        super.stop()
        context.stop()
    }

    override fun changeRequestPeriod(periodSec: Float) = context.changeRequestPeriod(periodSec)
    override suspend fun connect(ip: String, port: Int): Result<Unit> = context.connect(ip, port)
    override fun runModel(onResult: suspend (Result<Unit>) -> Unit) = context.runModel(onResult)
    override fun pauseModel(onResult: suspend (Result<Unit>) -> Unit) = context.pauseModel(onResult)
    override fun resumeModel(onResult: suspend (Result<Unit>) -> Unit) = context.resumeModel(onResult)
    override fun stopModel(onResult: suspend (Result<Unit>) -> Unit) = context.stopModel(onResult)
    override suspend fun disconnect() = context.disconnect()
}




