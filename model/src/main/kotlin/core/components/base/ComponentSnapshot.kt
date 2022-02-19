package core.components.base

data class ComponentSnapshot(
    val compClass: String = "",
    val immutableProps: MutableList<Property> = mutableListOf(),
    val mutableProps: MutableList<Property> = mutableListOf()
)