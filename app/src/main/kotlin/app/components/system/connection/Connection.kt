package app.components.system.connection

import app.components.system.base.Native
import core.components.base.AddToSnapshot
import core.components.base.Component
import core.components.base.TargetEntity
import core.entities.Environment

@TargetEntity(Environment::class)
class Connection: Component(), Native {
    @AddToSnapshot
    var authToken: String = ""
}