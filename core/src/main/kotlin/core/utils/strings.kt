package core.utils

import java.util.*

private val bundle by lazy {
    ResourceBundle.getBundle("strings", Locale.forLanguageTag("ru-RU"))
}

fun getString(key: String, vararg args: Any?): String {
    return bundle.getString(key).format(*args)
}
fun getString(key: String, locale: Locale?, vararg args: Any?): String {
    return bundle.getString(key).format(locale, *args)
}