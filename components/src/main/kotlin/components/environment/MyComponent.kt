package components.environment

import core.components.base.AddToSnapshot
import core.components.base.TargetEntity
import core.entities.Environment
import java.lang.Exception

@TargetEntity(Environment::class)
class MyComponent {
    @AddToSnapshot
    var a = 3
    set(value) {
        if (value < 0) throw Exception("value should be positive")
        field = value
    }
}