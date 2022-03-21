package core.utils

import core.services.logger.Level
import core.services.logger.Logger
import java.util.*

fun String.lowercaseFirstChar() = replaceFirstChar {
    if (it.isUpperCase()) it.lowercase(Locale.getDefault()) else it.toString()
}

fun String.uppercaseFirstChar() = replaceFirstChar {
    if (it.isLowerCase()) it.uppercase(Locale.getDefault()) else it.toString()
}

fun String.splitOnCapitalChars() = replace("([^_])([A-Z])".toRegex(), "$1 $2").uppercaseFirstChar()

fun runBlockCatching(block: () -> Unit) {
    try {
        block()
    } catch (e: Exception) {
        e.printStackTrace()
        Logger.log(e.stackTraceToString(), Level.ERROR)
    }
}