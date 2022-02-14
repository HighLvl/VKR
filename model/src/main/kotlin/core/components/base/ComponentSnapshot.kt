package core.components.base

data class ComponentSnapshot(val immutableProps: List<Property> = listOf(), val mutableProps: List<Property> = listOf())