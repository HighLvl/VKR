package app.services.model.control

import app.api.dto.Requests
import app.api.dto.Responses

interface IPModelAPI {
    suspend fun connect(ip: String, port: Int)

    fun disconnect()

    suspend fun handleRequests(requests: Requests): Responses
}
