package controllers

abstract class Controller {
    private enum class State {
        RUN, STOP
    }
    private var state = State.STOP

    open fun start() {
        if (state == State.RUN) {
            throw IllegalStateException("Stop before start")
        }
        state = State.RUN
    }
    open fun update() {
        if (state == State.STOP) {
            throw IllegalStateException("Start before update")
        }
    }

    open fun stop() {
        state = State.STOP
    }
}