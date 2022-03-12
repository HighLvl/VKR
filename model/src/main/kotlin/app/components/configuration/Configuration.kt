package app.components.configuration

import app.utils.KtsScriptEngine
import core.components.AddInSnapshot
import core.components.Script
import core.components.SystemComponent
import core.components.configuration.AgentInterfaces
import core.components.configuration.GlobalArgsComponent
import core.services.logger.Level
import core.services.logger.Logger

class Configuration : SystemComponent, GlobalArgsComponent, AgentInterfaces, Script {
    @AddInSnapshot(1)
    var modelConfiguration: String = ""
        set(value) {
            field = tryToLoadConfiguration(value, field)
        }

    @AddInSnapshot(2)
    override var globalArgs: Map<String, Any> = mapOf()

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
            globalArgs = _configuration.globalArgs
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