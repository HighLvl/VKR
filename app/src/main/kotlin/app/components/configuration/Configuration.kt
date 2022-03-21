package app.components.configuration

import app.utils.KtsScriptEngine
import core.components.base.AddInSnapshot
import core.components.base.Script
import app.components.base.SystemComponent
import core.components.configuration.AgentInterface
import core.components.configuration.InputArgsComponent
import core.components.configuration.ModelConfiguration
import core.components.configuration.MutableModelConfiguration
import core.services.logger.Level
import core.services.logger.Logger

class Configuration : SystemComponent, InputArgsComponent, AgentInterfaces, Script {
    @AddInSnapshot(1)
    var modelConfiguration: String = ""
        set(value) {
            field = tryToLoadConfiguration(value, field)
        }

    @AddInSnapshot(2)
    override var inputArgs: Map<String, Any> = mapOf()

    override val agentInterfaces: Map<String, AgentInterface>
        get() = _configuration.agentInterfaces

    private var _configuration: ModelConfiguration = MutableModelConfiguration()

    private var isRunning = false

    private fun tryToLoadConfiguration(path: String, oldPath: String): String {
        if(isRunning) {
            Logger.log("Stop model before loading configuration", Level.ERROR)
            return oldPath
        }
        if (path.isEmpty()) return oldPath
        return try {
            _configuration = KtsScriptEngine.eval(path)
            inputArgs = _configuration.inputArgs
            path
        } catch (e: Exception) {
            Logger.log("Bad configuration file", Level.ERROR)
            oldPath
        }
    }

    override fun onModelRun() {
        isRunning = true
    }

    override fun onModelStop() {
        isRunning = false
    }
}