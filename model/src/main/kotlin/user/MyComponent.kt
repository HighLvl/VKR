package user

import core.components.Component
import java.lang.Exception

class MyComponent: Component() {
    var a = 3
    set(value) {
        if (value < 0) throw Exception("value should be positive")
        field = value
    }
}