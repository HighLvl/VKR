package core.components.configuration

import core.components.base.Component

interface GlobalArgsComponent : Component {
    val globalArgs: Map<String, Any>
}