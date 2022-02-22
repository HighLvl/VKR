package core.components

interface Script {
    fun start() {}

    fun update(modelTime: Float) {}

    fun afterUpdate() {}

    fun stop() {}
}