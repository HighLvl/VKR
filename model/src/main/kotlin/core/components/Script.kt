package core.components

interface Script {
    fun onModelRun() {}
    fun onModelUpdate(modelTime: Float) {}
    fun onModelAfterUpdate() {}
    fun onModelStop() {}
    fun updateUI() {}
    fun onModelPause() {}
    fun onModelResume() {}
}