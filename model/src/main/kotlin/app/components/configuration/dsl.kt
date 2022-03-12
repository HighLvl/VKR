package app.components.configuration

import app.utils.uppercaseFirstChar
import kotlin.reflect.KClass

class ModelConfigurationContext(private val modelConfiguration: MutableModelConfiguration) {
    fun agentInterface(name: String, builder: AgentInterfaceContext.() -> Unit) {
        modelConfiguration.addAgentInterface(
            MutableAgentInterface(name).apply { AgentInterfaceContext(this).apply(builder) }
        )
    }

    fun globalArg(name: String, value: Any) {
        modelConfiguration.putGlobalArg(name, value)
    }

    fun globalArgs(vararg args: Pair<String, Any>) {
        args.forEach { modelConfiguration.putGlobalArg(it.first, it.second) }
    }
}

class AgentInterfaceContext(private val agentInterface: MutableAgentInterface) {
    fun <T : Any> request(returnType: KClass<T>, name: String, builder: RequestSignatureContext.() -> Unit = {}) {
        agentInterface.addRequest(
            MutableRequestSignature(
                name,
                returnType
            ).apply { RequestSignatureContext(this).apply(builder) })
    }

    fun <T : Any> setter(valueType: KClass<T>, varName: String) =
        agentInterface.addSetter(
            MutableRequestSignature(
                "Set${varName.uppercaseFirstChar()}",
                Unit::class
            ).apply { addParam("value", valueType) })

    fun properties(vararg names: String) {
        names.forEach { agentInterface.addProperty(it) }
    }

    inline fun <reified T : Any> request(name: String, noinline builder: RequestSignatureContext.() -> Unit = {}) =
        request(T::class, name, builder)

    inline fun <reified T : Any> setter(varName: String) = setter(T::class, varName)
}

class RequestSignatureContext(private val requestSignature: MutableRequestSignature) {
    fun <T : Any> param(paramType: KClass<T>, name: String) = requestSignature.addParam(name, paramType)
    inline fun <reified T : Any> param(name: String) = param(T::class, name)
}

fun modelConfiguration(builder: ModelConfigurationContext.() -> Unit): ModelConfiguration =
    MutableModelConfiguration().apply { ModelConfigurationContext(this).apply(builder) }

