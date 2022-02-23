package app.services.model.control.state

import core.api.AgentModelApiClient
import core.api.dto.GlobalArgs
import core.api.dto.Snapshot
import core.coroutines.PeriodTaskExecutor
import core.services.AgentModelLifecycleEvent
import core.services.EventBus
import core.services.SnapshotReceive
import core.services.listen

class AgentModelControlContext(val apiClient: AgentModelApiClient) {
    private var currentState: State = DisconnectState

    val periodTaskExecutor = PeriodTaskExecutor()

    var globalArgs = GlobalArgs(mutableMapOf())
    var periodSec: Float = 0.1f

    fun start() {
        subscribeOnGlobalArgs()
    }

    private fun subscribeOnGlobalArgs() {
        EventBus.listen<AgentModelLifecycleEvent.GlobalArgsSet>().subscribe() {
            globalArgs = it.args
        }
    }

    suspend fun connect(ip: String, port: Int) = currentState.connect(this, ip, port)


    suspend fun disconnect() {
        currentState.disconnect(this)
    }

    suspend fun run() {
        currentState.run(this, periodSec)
    }

    suspend fun pause() {
        currentState.pause(this)
    }

    suspend fun resume() {
        currentState.resume(this)
    }

    suspend fun stop() {
        currentState.stop(this)
    }

    fun setState(state: State) {
        this.currentState = state
    }

    fun onPause() {
        EventBus.publish(AgentModelLifecycleEvent.Pause)
    }

    fun onStart() {
        EventBus.publish(AgentModelLifecycleEvent.Start)
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
}