package app.api.base


import app.api.dto.Requests
import app.api.dto.Responses

interface AgentModelApi {
    suspend fun connect(ip: String, port: Int)
    fun disconnect()
    suspend fun handleRequests(requests: Requests) : Responses
}