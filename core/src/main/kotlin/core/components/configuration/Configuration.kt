package core.components.configuration

import core.components.base.Component

interface InputArgsComponent : Component {
    val inputArgs: Map<String, Any>
}