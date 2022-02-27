package views

import core.uppercaseFirstChar

fun String.splitOnCapitalLetters() = replace("([^_])([A-Z])".toRegex(), "$1 $2").uppercaseFirstChar()