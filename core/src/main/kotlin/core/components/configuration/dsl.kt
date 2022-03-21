package core.components.configuration

import core.utils.uppercaseFirstChar
import kotlin.reflect.KClass

@DslMarker
annotation class ConfigurationDslMarker

@ConfigurationDslMarker
class ModelConfigurationBuilder(private val modelConfiguration: MutableModelConfiguration) {
    fun agentInterface(name: String, builder: AgentInterfaceBuilder.() -> Unit) {
        modelConfiguration.addAgentInterface(
            MutableAgentInterface(name).apply { AgentInterfaceBuilder(this).apply(builder) }
        )
    }

    fun inputArgs(builder: InputArgsBuilder.() -> Unit) {
        InputArgsBuilder(modelConfiguration).apply(builder)
    }
}

@ConfigurationDslMarker
class InputArgsBuilder(private val modelConfiguration: MutableModelConfiguration) {
    fun arg(name: String, value: Any) {
        modelConfiguration.putInputArg(name, value)
    }
}

@ConfigurationDslMarker
class AgentInterfaceBuilder(private val agentInterface: MutableAgentInterface) {
    fun <T : Any> request(returnType: KClass<T>, name: String, builder: RequestSignatureBuilder.() -> Unit = {}) {
        agentInterface.addRequest(
            MutableRequestSignature(
                name,
                returnType
            ).apply { RequestSignatureBuilder(this).apply(builder) })
    }

    fun <T : Any> setter(valueType: KClass<T>, varName: String) =
        agentInterface.addSetter(
            MutableRequestSignature(
                "Set${varName.uppercaseFirstChar()}",
                Unit::class
            ).apply { addParam("value", valueType) })

    fun prop(name: String) {
        agentInterface.addProperty(Property(name))
    }

    inline fun <reified T : Any> request(name: String, noinline builder: RequestSignatureBuilder.() -> Unit = {}) =
        request(T::class, name, builder)

    inline fun <reified T : Any> setter(varName: String) = setter(T::class, varName)
}

@ConfigurationDslMarker
class RequestSignatureBuilder(private val requestSignature: MutableRequestSignature) {
    fun <T : Any> param(paramType: KClass<T>, name: String) = requestSignature.addParam(name, paramType)
    inline fun <reified T : Any> param(name: String) = param(T::class, name)
}

fun modelConfiguration(build: ModelConfigurationBuilder.() -> Unit): ModelConfiguration =
    MutableModelConfiguration().apply { ModelConfigurationBuilder(this).build() }

