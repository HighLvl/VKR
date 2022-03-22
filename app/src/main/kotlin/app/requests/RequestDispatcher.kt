package app.requests

import app.api.dto.Requests
import app.api.dto.Responses

interface RequestDispatcher {
    fun commitRequests(): Requests
    fun handleResponses(responses: Responses)
    fun clear()
}