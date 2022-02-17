package core.components.base

import kotlin.reflect.KClass

data class ComponentSnapshot(
    val compClass: String = "",
    val immutableProps: List<Property> = listOf(),
    val mutableProps: List<Property> = listOf()
)