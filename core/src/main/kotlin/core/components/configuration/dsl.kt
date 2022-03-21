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

    fun inputArg(name: String, value: Any) {
        modelConfiguration.putInputArg(name, value)
    }

    fun inputArgs(vararg args: Pair<String, Any>) {
        args.forEach { modelConfiguration.putInputArg(it.first, it.second) }
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

    fun structProp(name: String, builder: PropBuilder.() -> Unit) {
        val strPropBuilder = StructPropertyBuilder(name)
        val propBuilder = PropBuilder(strPropBuilder)
        propBuilder.apply(builder)
        agentInterface.addProperty(strPropBuilder.build())
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

class StructPropertyBuilder(private val name: String) {
    private val properties = mutableListOf<Prop>()

    fun addProp(prop: Prop) {
        properties.add(prop)
    }

    fun build() : StructProperty {
        return StructProperty(name, properties)
    }
}

@ConfigurationDslMarker
class PropBuilder(private val structPropertyBuilder: StructPropertyBuilder) {
    fun prop(name: String) {
        structPropertyBuilder.addProp(Property(name))
    }

    fun structProp(name: String, builder: PropBuilder.() -> Unit) {
        val strPropBuilder = StructPropertyBuilder(name)
        val propBuilder = PropBuilder(strPropBuilder)
        propBuilder.apply(builder)
        structPropertyBuilder.addProp(strPropBuilder.build())
    }
}

fun modelConfiguration(build: ModelConfigurationBuilder.() -> Unit): ModelConfiguration =
    MutableModelConfiguration().apply { ModelConfigurationBuilder(this).build() }

