package app.api.base

import kotlinx.coroutines.flow.Flow
import app.api.dto.Behaviour
import app.api.dto.GlobalArgs
import app.api.dto.Snapshot
import app.api.dto.State


interface AgentModelApi {
    fun connect(ip: String, port: Int): State
    fun disconnect()
    fun run(globalArgs: GlobalArgs)
    fun runAndSubscribeOnUpdate(globalArgs: GlobalArgs): Flow<Unit>
    fun callBehaviourFunctions(behaviour: Behaviour)
    fun requestSnapshot(): Snapshot
    fun pause()
    fun resume()
    fun stop()
}