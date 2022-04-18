package gui.controllers

abstract class Controller {
    open fun onPreRun() {}
    open fun update() {}
    open fun stop() {}
}