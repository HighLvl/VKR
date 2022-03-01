package app.services.model.control.state

import app.services.model.control.ControlState
import core.api.AgentModelApiClient
import core.api.dto.GlobalArgs
import core.api.dto.Snapshot
import core.coroutines.PeriodTaskExecutor
import core.coroutines.launchWithAppContext
import core.services.*
import io.reactivex.rxjava3.disposables.Disposable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.onSubscription
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class AgentModelControlContext(val apiClient: AgentModelApiClient) {
    val controlState: MutableSharedFlow<ControlState> = MutableSharedFlow<ControlState>().apply {
        onSubscription { emit(getControlState()) }
    }
    val periodTaskExecutor = PeriodTaskExecutor()
    var globalArgs = GlobalArgs(mutableMapOf())
    var periodSec: Float = 0.1f

    private lateinit var currentState: State
    val disposables = mutableListOf<Disposable>()

    fun start() {
        subscribeOnGlobalArgs()
        subscribeOnBehaviourRequestsReady()
        setState(DisconnectState)
    }

    private fun subscribeOnBehaviourRequestsReady() {
        disposables += EventBus.listen<BehaviourRequestsReady>().subscribe {
            runBlocking {
                withContext(Dispatchers.IO) {
                    apiClient.callBehaviourFunctions(it.behaviour)
                }
            }
        }
    }

    private fun subscribeOnGlobalArgs() {
        disposables += EventBus.listen<GlobalArgsSet>().subscribe {
            globalArgs = it.args
        }
    }

    suspend fun connect(ip: String, port: Int) = currentState.connect(this, ip, port)

    suspend fun disconnect() {
        currentState.disconnect(this)
    }

    suspend fun runModel() {
        currentState.run(this, periodSec)
    }

    suspend fun pauseModel() {
        currentState.pause(this)
    }

    suspend fun resumeModel() {
        currentState.resume(this)
    }

    suspend fun stopModel() {
        currentState.stop(this)
    }

    fun setState(state: State) {
        this.currentState = state
        launchWithAppContext {
            controlState.emit(getControlState())
        }
    }

    private fun getControlState(): ControlState = when (currentState::class) {
        DisconnectState::class -> ControlState.DISCONNECT
        ConnectState::class -> ControlState.CONNECT
        StopState::class -> ControlState.STOP
        RunState::class -> ControlState.RUN
        PauseState::class -> ControlState.PAUSE
        else -> throw IllegalStateException()
    }


    fun onPause() {
        EventBus.publish(AgentModelLifecycleEvent.Pause)
    }

    fun onStart() {
        EventBus.publish(AgentModelLifecycleEvent.Run)
    }

    fun onResume() {
        EventBus.publish(AgentModelLifecycleEvent.Resume)
    }

    fun onStop() {
        EventBus.publish(AgentModelLifecycleEvent.Stop)
    }

    fun onUpdate(snapshot: Snapshot) {
        EventBus.publish(SnapshotReceive(snapshot))
    }

    fun changeRequestPeriod(periodSec: Float) {
        periodTaskExecutor.changePeriod(periodSec)
    }

    fun stop() {
        periodTaskExecutor.stop()
        disposables.forEach { it.dispose() }
        disposables.clear()
    }
}