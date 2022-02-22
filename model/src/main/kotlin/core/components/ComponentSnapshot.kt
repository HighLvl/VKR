package core.components

data class ComponentSnapshot(
    val compClass: String = "",
    val immutableProps: MutableList<Property> = mutableListOf(),
    val mutableProps: MutableList<Property> = mutableListOf()
)