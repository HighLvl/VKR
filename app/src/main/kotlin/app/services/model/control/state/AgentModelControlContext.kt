package app.services.model.control.state

import core.services.control.ControlState
import app.services.scene.SceneApi
import app.api.base.AgentModelApi
import app.api.dto.Behaviour
import app.api.dto.GlobalArgs
import app.api.dto.Snapshot
import app.coroutines.Contexts
import app.services.model.control.PeriodTaskExecutor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AgentModelControlContext(val modelApi: AgentModelApi, private val sceneApi: SceneApi) {
    val controlState: MutableSharedFlow<ControlState> = MutableSharedFlow(replay = 1)
    val periodTaskExecutor = PeriodTaskExecutor(Contexts.app)
    val globalArgs: GlobalArgs
        get() = sceneApi.getGlobalArgs()
    private var periodSec: Float = 0.1f
    private lateinit var currentState: State

    val coroutineScope = CoroutineScope(Contexts.app)

    fun start() {
        setState(DisconnectState)
    }

    fun onPause() = sceneApi.onModelPause()
    fun onStart() = sceneApi.onModelRun()
    fun onResume() = sceneApi.onModelResume()
    fun onStop() = sceneApi.onModelStop()
    fun onUpdate(snapshot: Snapshot) = sceneApi.updateWith(snapshot)
    fun getBehaviour(): Behaviour = sceneApi.getBehaviour()

    fun connect(ip: String, port: Int) = currentState.connect(this, ip, port)
    fun disconnect() = currentState.disconnect(this)
    private var startTime: Long = 0
    fun runModel() {
        startTime = System.currentTimeMillis()
        currentState.run(this, periodSec)
    }

    fun pauseModel() = currentState.pause(this)
    fun resumeModel() = currentState.resume(this)
    fun stopModel() {
        currentState.stop(this)
        println(System.currentTimeMillis() - startTime)
    }

    fun setState(state: State) {
        this.currentState = state
        coroutineScope.launch {
            withContext(Dispatchers.IO) {controlState.emit(getControlState())}
        }
    }

    private fun getControlState(): ControlState = when (currentState::class) {
        DisconnectState::class -> ControlState.DISCONNECT
        StopState::class -> ControlState.STOP
        RunState::class -> ControlState.RUN
        PauseState::class -> ControlState.PAUSE
        else -> throw IllegalStateException()
    }

    fun changeRequestPeriod(periodSec: Float) {
        if (periodSec < 0) throw IllegalArgumentException("Period must be more than zero")
        periodTaskExecutor.changePeriod(periodSec)
    }

    fun stop() {
        periodTaskExecutor.stop()
    }
}