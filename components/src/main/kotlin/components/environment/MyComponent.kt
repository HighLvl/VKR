package components.environment

import core.components.base.AddInSnapshot
import core.components.base.Component
import core.components.base.TargetEntity
import core.entities.Environment
import java.lang.Exception

@TargetEntity(Environment::class)
class MyComponent: Component {
    @AddInSnapshot
    var a = 3
    set(value) {
        if (value < 0) throw Exception("value should be positive")
        field = value
    }
}