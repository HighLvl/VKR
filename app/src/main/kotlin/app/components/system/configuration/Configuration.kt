package app.components.system.configuration

import app.components.system.base.Native
import app.utils.KtsScriptEngine
import core.components.base.AddInSnapshot
import core.components.base.Component
import core.components.base.TargetEntity
import core.components.configuration.AgentInterface
import core.components.configuration.Configuration
import core.components.configuration.InputArgs
import core.components.configuration.ModelConfiguration
import core.entities.Environment
import core.services.Services
import core.services.control.ControlState
import core.services.logger.Level
import core.services.logger.Logger
import core.services.putInputArg
import core.utils.getString
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@TargetEntity(Environment::class)
class Configuration : Component(), Native, InputArgs, AgentInterfaces, Configuration {
    @AddInSnapshot(1)
    override var modelConfiguration: String = ""
        set(value) {
            field = tryToLoadConfiguration(value, field)
        }

    @AddInSnapshot(2)
    override var inputArgs: Map<String, Any>
        get() = _inputArgs
        set(value) {
            value.forEach {
                putInputArg(it.key, it.value)
            }
        }

    override val agentInterfaces: StateFlow<Map<String, AgentInterface>>
        get() = _agentInterfaces

    private val _agentInterfaces = MutableStateFlow<Map<String, AgentInterface>>(mapOf())
    private val _inputArgs = mutableMapOf<String, Any>()

    private fun tryToLoadConfiguration(path: String, oldPath: String): String {
        when (Services.agentModelControl.controlState) {
            ControlState.RUN, ControlState.PAUSE -> {
                Logger.log(getString("stop_model_before_loading_conf"), Level.ERROR)
                return oldPath
            }
            else -> {
            }
        }
        if (path.isEmpty()) return oldPath
        return try {
            val configuration = KtsScriptEngine().eval<ModelConfiguration>(path)
            with(_inputArgs) {
                clear()
                putAll(configuration.inputArgs)
            }
            _agentInterfaces.value = configuration.agentInterfaces
            path
        } catch (e: Exception) {
            Logger.log(getString("bad_configuration_file"), Level.ERROR)
            Logger.log(e.stackTraceToString(), Level.ERROR)
            oldPath
        }
    }

    override fun put(name: String, value: Any) {
        if (name !in _inputArgs || _inputArgs[name]!!::class != value::class) return
        _inputArgs[name] = value
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : Any> get(name: String): T {
        return _inputArgs[name]!! as T
    }
}