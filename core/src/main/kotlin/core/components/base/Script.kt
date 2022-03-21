package core.components.base

interface Script {
    fun onModelRun() {}
    fun onModelUpdate(modelTime: Double) {}
    fun onModelAfterUpdate() {}
    fun onModelStop() {}
    fun updateUI() {}
    fun onModelPause() {}
    fun onModelResume() {}
}