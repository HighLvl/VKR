package app.api.base

import kotlinx.coroutines.flow.Flow
import app.api.dto.Behaviour
import app.api.dto.GlobalArgs
import app.api.dto.Snapshot
import app.api.dto.State


interface AgentModelApi {
    suspend fun connect(ip: String, port: Int): State
    fun disconnect()
    suspend fun run(globalArgs: GlobalArgs)
    suspend fun runAndSubscribeOnUpdate(globalArgs: GlobalArgs): Flow<Unit>
    suspend fun callBehaviourFunctions(behaviour: Behaviour)
    suspend fun requestSnapshot(): Snapshot
    suspend fun pause()
    suspend fun resume()
    suspend fun stop()
}