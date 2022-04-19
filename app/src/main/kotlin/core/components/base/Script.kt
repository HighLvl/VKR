package core.components.base

interface Script {
    fun onModelRun() {}
    fun onModelUpdate() {}
    fun onModelAfterUpdate() {}
    fun onModelStop() {}
    fun updateUI() {}
    fun onModelPause() {}
    fun onModelResume() {}
}