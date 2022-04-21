package app.services.scene

import app.api.dto.ModelInputArgs
import app.api.dto.Snapshot

interface SceneApi {
    fun getInputArgs(): ModelInputArgs
    suspend fun updateWith(snapshot: Snapshot)
    suspend fun onModelRun()
    suspend fun onModelStop()
    suspend fun onModelPause()
    suspend fun onModelResume()
}