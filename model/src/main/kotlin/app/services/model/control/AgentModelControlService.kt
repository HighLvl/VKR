package app.services.model.control


import app.services.model.control.state.AgentModelControlContext
import core.api.AgentModelApi
import app.services.Service
import app.services.scene.SceneApi
import kotlinx.coroutines.flow.SharedFlow

class AgentModelControlService(modelApi: AgentModelApi, sceneApi: SceneApi) : Service(), AgentModelControl {
        private val context = AgentModelControlContext(modelApi, sceneApi)
        override val controlState: SharedFlow<ControlState> = context.controlState

    override fun start() {
        super.start()
        context.start()
    }

    override fun stop() {
        super.stop()
        context.stop()
    }

    override fun changeRequestPeriod(periodSec: Float) = context.changeRequestPeriod(periodSec)
    override fun connect(ip: String, port: Int) = context.connect(ip, port)
    override fun runModel() = context.runModel()
    override fun pauseModel() = context.pauseModel()
    override fun resumeModel() = context.resumeModel()
    override fun stopModel() = context.stopModel()
    override fun disconnect() = context.disconnect()
}




