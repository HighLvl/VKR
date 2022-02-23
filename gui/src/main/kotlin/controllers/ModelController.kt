package controllers

import app.logger.Log
import app.logger.Logger
import app.services.model.control.AgentModelControlService
import core.api.dto.State
import core.coroutines.launchWithAppContext
import views.ModelControlView

class ModelController(
    private val modelControlView: ModelControlView,
    private val modelControlService: AgentModelControlService
) {
    init {
        modelControlView.disconnect()
        modelControlView.apply {
            onClickConnectListener = { ip, port ->
                launchWithAppContext {
                    Logger.log("ip: $ip, port: $port", Log.Level.INFO)
                    disableAll()
                    val state = modelControlService.connect(ip, port)
                    when(state) {
                        State.RUN -> run()
                        State.PAUSE -> pause()
                        State.STOP -> connect()
                    }
                }
            }
            onClickPauseListener = {
                launchWithAppContext {
                    disableAll()
                    modelControlService.pause()
                    pause()
                }
            }
            onClickRunListener = {
                launchWithAppContext {
                    disableAll()
                    modelControlService.run()
                    run()
                }
            }
            onClickResumeListener = {
                launchWithAppContext {
                    disableAll()
                    modelControlService.resume()
                    run()
                }
            }
            onClickStopListener = {
                launchWithAppContext {
                    disableAll()
                    modelControlService.stop()
                    stop()
                }
            }
            onClickDisconnectListener = {
                launchWithAppContext {
                    disableAll()
                    modelControlService.disconnect()
                    disconnect()
                }
            }
            onChangeDtListener = {
                modelControlService.changeRequestPeriod(it)
            }
        }

    }
}