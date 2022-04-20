package app.utils

import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.full.declaredFunctions
import kotlin.reflect.jvm.isAccessible

internal fun <T : Any> getAccessibleFunction(kClass: KClass<T>, name: String): KFunction<*> {
    return kClass
        .declaredFunctions
        .find { it.name == name }!!.apply { isAccessible = true }
}

internal inline fun <reified T : Any> getAccessibleFunction(name: String): KFunction<*> {
    return getAccessibleFunction(T::class, name)
}