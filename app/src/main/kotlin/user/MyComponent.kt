package user

import core.components.base.AddInSnapshot
import core.components.base.Component
import java.lang.Exception

class MyComponent: Component {
    @AddInSnapshot
    var a = 3
    set(value) {
        if (value < 0) throw Exception("value should be positive")
        field = value
    }
}