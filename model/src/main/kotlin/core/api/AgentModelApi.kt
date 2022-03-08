package core.api

import kotlinx.coroutines.flow.Flow
import core.api.dto.Behaviour
import core.api.dto.GlobalArgs
import core.api.dto.Snapshot
import core.api.dto.State


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