package core.utils

import core.services.logger.Level
import core.services.logger.Logger
import java.util.*
import kotlin.reflect.KClass
import kotlin.reflect.full.isSubclassOf

fun String.lowercaseFirstChar() = replaceFirstChar {
    if (it.isUpperCase()) it.lowercase(Locale.getDefault()) else it.toString()
}

fun String.uppercaseFirstChar() = replaceFirstChar {
    if (it.isLowerCase()) it.uppercase(Locale.getDefault()) else it.toString()
}

fun String.splitOnCapitalChars() = replace("([^_])([A-Z])".toRegex(), "$1 $2").uppercaseFirstChar()

inline fun runBlockCatching(block: () -> Unit) {
    try {
        block()
    } catch (e: Exception) {
        e.printStackTrace()
        Logger.log(e.stackTraceToString(), Level.ERROR)
    }
}

private val subclassCache = mutableMapOf<Pair<KClass<*>, KClass<*>>, Boolean>()

fun KClass<*>.isSubclass(base: KClass<*>): Boolean {
    val key = this to base
    subclassCache[key]?.let {
        return it
    }
    return isSubclassOf(base).also { subclassCache[key] = it }
}