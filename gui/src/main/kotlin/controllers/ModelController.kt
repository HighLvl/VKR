package controllers

import app.logger.Log
import app.logger.Logger
import app.services.model.control.AgentModelControlService
import app.services.model.control.ControlState
import core.coroutines.launchWithAppContext
import kotlinx.coroutines.Job
import views.ModelControlView

class ModelController(
    private val modelControlView: ModelControlView,
    private val modelControlService: AgentModelControlService
): Controller() {
    private var collectModelControlStateJob: Job? = null

    override fun start() {
        super.start()
        collectModelControlStateJob = launchWithAppContext {
            modelControlService.controlState.collect { state ->
                when (state) {
                    ControlState.DISCONNECT -> modelControlView.disconnect()
                    ControlState.CONNECT -> modelControlView.connect()
                    ControlState.RUN -> modelControlView.run()
                    ControlState.STOP -> modelControlView.stop()
                    ControlState.PAUSE -> modelControlView.pause()
                }
            }
        }
        modelControlView.apply {
            onClickConnectListener = { ip, port ->
                launchWithAppContext {
                    Logger.log("ip: $ip, port: $port", Log.Level.INFO)
                    disableAll()
                    modelControlService.connect(ip, port)
                }
            }
            onClickPauseListener = {
                launchWithAppContext {
                    disableAll()
                    modelControlService.pause()
                }
            }
            onClickRunListener = {
                launchWithAppContext {
                    disableAll()
                    modelControlService.run()
                }
            }
            onClickResumeListener = {
                launchWithAppContext {
                    disableAll()
                    modelControlService.resume()
                }
            }
            onClickStopListener = {
                launchWithAppContext {
                    disableAll()
                    modelControlService.stop()
                }
            }
            onClickDisconnectListener = {
                launchWithAppContext {
                    disableAll()
                    modelControlService.disconnect()
                }
            }
            onChangeDtListener = {
                modelControlService.changeRequestPeriod(it)
            }
        }
    }

    override fun stop() {
        super.stop()
        collectModelControlStateJob?.cancel()
        modelControlView.apply {
            onClickConnectListener = {_, _ ->}
            onClickPauseListener = { }
            onClickRunListener = { }
            onClickResumeListener = { }
            onClickStopListener = { }
            onClickDisconnectListener = { }
            onChangeDtListener = { }
        }
    }
}