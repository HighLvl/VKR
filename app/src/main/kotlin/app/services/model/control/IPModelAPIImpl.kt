package app.services.model.control

import app.api.ApiImpl
import app.api.dto.Requests
import app.api.dto.Responses
import app.components.system.connection.Connection
import core.entities.getComponent
import core.services.Services

class IPModelAPIImpl : IPModelAPI {
    private var apiClient: ApiImpl? = null

    override suspend fun connect(ip: String, port: Int) {
        if (apiClient != null) return

        val token = getAuthToken()
        apiClient = ApiImpl(ip, port, token).apply { connect() }
    }

    override fun disconnect() {
        apiClient?.disconnect()
        apiClient = null
    }

    override suspend fun handleRequests(requests: Requests): Responses {
        return apiClient!!.handleRequests(requests)
    }

    private fun getAuthToken(): String {
        return Services.scene.environment.getComponent<Connection>()!!.authToken
    }
}