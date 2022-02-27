package core.components

interface Script {
    fun start() {}
    fun onModelUpdate(modelTime: Float) {}
    fun update() {}
    fun onModelAfterUpdate() {}
    fun stop() {}
}