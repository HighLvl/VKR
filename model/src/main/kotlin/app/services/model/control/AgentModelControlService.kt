package app.services.model.control


import app.services.user.AgentModelControl
import app.services.model.control.state.AgentModelControlContext
import core.api.AgentModelApiClient
import core.coroutines.AppContext
import core.services.Service
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

class AgentModelControlService(apiClient: AgentModelApiClient) : Service(), AgentModelControl {
    private val context = AgentModelControlContext(apiClient)
    val controlState: SharedFlow<ControlState> = context.controlState

    private val mutex = Mutex()

    override fun start() {
        super.start()
        context.start()
    }

    override fun stop() {
        super.stop()
        context.stop()
    }

    override suspend fun changeRequestPeriod(periodSec: Float) = mutex.withLock {
        context.changeRequestPeriod(periodSec)
    }

    suspend fun connect(ip: String, port: Int) = mutex.withLock {
        context.connect(ip, port)
    }
    override suspend fun runModel() = mutex.withLock {
        context.runModel()
    }
    override suspend fun pauseModel() = mutex.withLock {
        context.pauseModel()
    }
    override suspend fun resumeModel() = mutex.withLock {
        context.resumeModel()
    }
    override suspend fun stopModel() = mutex.withLock {
        context.stopModel()
    }
    suspend fun disconnect() = mutex.withLock {
        context.disconnect()
    }
}




