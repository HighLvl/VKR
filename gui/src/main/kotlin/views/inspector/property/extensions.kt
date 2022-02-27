package views.inspector.property

import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.ObjectNode

fun ObjectNode.put(name: String, value: Any) {
    when (value) {
        is Int -> put(name, value)
        is Double -> put(name, value)
        is String -> put(name, value)
        is Boolean -> put(name, value)
        else -> { }
    }
}

fun ArrayNode.insert(index: Int, value: Any) {
    when (value) {
        is Int -> insert(index, value)
        is Double -> insert(index, value)
        is String -> insert(index, value)
        else -> {
        }
    }
}

