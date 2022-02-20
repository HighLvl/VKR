package views.inspector

fun String.splitOnCapitalLetters() = replace("([^_])([A-Z])".toRegex(), "$1 $2")