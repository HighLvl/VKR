package core.api

import kotlinx.coroutines.flow.Flow
import core.api.dto.Behaviour
import core.api.dto.GlobalArgs
import core.api.dto.Snapshot
import core.api.dto.State


interface AgentModelApiClient {
    suspend fun connect(ip: String, port: Int): State
    suspend fun disconnect()
    suspend fun run(globalArgs: GlobalArgs)
    suspend fun runAndSubscribeOnUpdate(globalArgs: GlobalArgs): Flow<Unit>
    suspend fun callBehaviourFunctions(behaviour: Behaviour)
    suspend fun requestSnapshot(): Snapshot
    suspend fun pause()
    suspend fun resume()
    suspend fun stop()
}