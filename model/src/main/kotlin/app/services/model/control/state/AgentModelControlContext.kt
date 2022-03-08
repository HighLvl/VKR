package app.services.model.control.state

import app.services.EventBus
import app.services.GlobalArgsSet
import app.services.listen
import app.services.model.control.ControlState
import app.services.scene.SceneApi
import core.api.AgentModelApi
import core.api.dto.Behaviour
import core.api.dto.GlobalArgs
import core.api.dto.Snapshot
import core.coroutines.Contexts
import core.coroutines.PeriodTaskExecutor
import io.reactivex.rxjava3.disposables.Disposable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

class AgentModelControlContext(val modelApi: AgentModelApi, private val sceneApi: SceneApi) {
    val controlState: MutableSharedFlow<ControlState> = MutableSharedFlow(replay = 1)
    val periodTaskExecutor = PeriodTaskExecutor(Contexts.app)
    var globalArgs = GlobalArgs(mutableMapOf())
    var periodSec: Float = 0.1f
    private val disposables = mutableListOf<Disposable>()
    private lateinit var currentState: State

    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    fun start() {
        subscribeOnGlobalArgs()
        setState(DisconnectState)
    }

    private fun subscribeOnGlobalArgs() {
        disposables += EventBus.listen<GlobalArgsSet>().subscribe {
            globalArgs = it.args
        }
    }

    fun onPause() = sceneApi.onModelPause()
    fun onStart() = sceneApi.onModelRun()
    fun onResume() = sceneApi.onModelResume()
    fun onStop() = sceneApi.onModelStop()
    fun onUpdate(snapshot: Snapshot) = sceneApi.updateSceneWith(snapshot)
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
            controlState.emit(getControlState())
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
        disposables.forEach { it.dispose() }
        disposables.clear()
    }
}