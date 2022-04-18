package gui.utils

import java.util.*

private val bundle by lazy {
    ResourceBundle.getBundle("strings", Locale.forLanguageTag("en"))
}

internal fun getString(key: String, vararg args: Any?): String {
    return bundle.getString(key).format(*args)
}