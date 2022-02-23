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
        modelControlView.apply {
            onClickConnectListener = { ip, port ->
                launchWithAppContext {
                    Logger.log("ip: $ip, port: $port", Log.Level.INFO)
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
                    modelControlService.pause()
                    pause()
                }
            }
            onClickRunListener = {
                launchWithAppContext {
                    modelControlService.run()
                    run()
                }
            }
            onClickResumeListener = {
                launchWithAppContext {
                    modelControlService.resume()
                    run()
                }
            }
            onClickStopListener = {
                launchWithAppContext {
                    modelControlService.stop()
                    stop()
                }
            }
            onClickDisconnectListener = {
                launchWithAppContext {
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