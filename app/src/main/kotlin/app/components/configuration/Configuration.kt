package app.components.configuration

import app.components.base.SystemComponent
import app.utils.KtsScriptEngine
import core.components.base.AddInSnapshot
import core.components.base.Script
import core.components.configuration.AgentInterface
import core.components.configuration.InputArgsComponent
import core.components.configuration.ModelConfiguration
import core.services.logger.Level
import core.services.logger.Logger
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class Configuration : SystemComponent, InputArgsComponent, AgentInterfaces, Script {
    @AddInSnapshot(1)
    var modelConfiguration: String = ""
        set(value) {
            field = tryToLoadConfiguration(value, field)
        }

    @AddInSnapshot(2)
    override var inputArgs: Map<String, Any> = mapOf()

    override val agentInterfaces: StateFlow<Map<String, AgentInterface>>
        get() = _agentInterfaces

    private val _agentInterfaces = MutableStateFlow<Map<String, AgentInterface>>(mapOf())
    private var isRunning = false

    private fun tryToLoadConfiguration(path: String, oldPath: String): String {
        if (isRunning) {
            Logger.log("Stop model before loading configuration", Level.ERROR)
            return oldPath
        }
        if (path.isEmpty()) return oldPath
        return try {
            val configuration = KtsScriptEngine.eval<ModelConfiguration>(path)
            inputArgs = configuration.inputArgs
            _agentInterfaces.value = configuration.agentInterfaces
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