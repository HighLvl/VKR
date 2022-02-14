package core

import java.util.*

fun String.lowercaseFirstChar() = replaceFirstChar {
    if (it.isUpperCase()) it.lowercase(Locale.getDefault()) else it.toString()
}

fun String.uppercaseFirstChar() = replaceFirstChar {
    if (it.isLowerCase()) it.uppercase(Locale.getDefault()) else it.toString()
}