package app.services.scene

import app.api.dto.ModelInputArgs
import app.api.dto.Snapshot

interface SceneApi {
    fun getInputArgs(): ModelInputArgs
    fun updateWith(snapshot: Snapshot)
    fun onModelRun()
    fun onModelStop()
    fun onModelPause()
    fun onModelResume()
}