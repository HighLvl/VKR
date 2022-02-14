package core.components.base

abstract class Script: Component() {
    open fun start() {}

    open fun update(modelTime: Float) {}

    open fun afterUpdate() {}

    open fun stop() {}
}