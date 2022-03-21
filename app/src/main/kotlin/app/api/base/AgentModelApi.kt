package app.api.base


import app.api.dto.InputData
import app.api.dto.Snapshot

interface AgentModelApi {
    suspend fun connect(ip: String, port: Int)
    fun disconnect()
    suspend fun getSnapshot(inputData: InputData) : Snapshot
}