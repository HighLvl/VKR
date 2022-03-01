package app.utils

import app.logger.Log
import app.logger.Logger
import java.util.*

fun String.lowercaseFirstChar() = replaceFirstChar {
    if (it.isUpperCase()) it.lowercase(Locale.getDefault()) else it.toString()
}

fun String.uppercaseFirstChar() = replaceFirstChar {
    if (it.isLowerCase()) it.uppercase(Locale.getDefault()) else it.toString()
}

fun String.splitOnCapitalLetters() = replace("([^_])([A-Z])".toRegex(), "$1 $2").uppercaseFirstChar()

fun runBlockCatching(block: () -> Unit) {
    try {
        block()
    } catch (e: Exception) {
        Logger.log(e.stackTraceToString(), Log.Level.ERROR)
    }
}