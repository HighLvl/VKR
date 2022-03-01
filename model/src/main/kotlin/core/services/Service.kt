package core.services

abstract class Service {
    private var stopped = true
    open fun start() {
        if (!stopped) {
            throw IllegalStateException("Stop Service before start")
        }
    }
    open fun stop() {}
}