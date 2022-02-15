package views.properties

abstract class MutableProperty<T>(
    name: String,
    protected val onValueChange: (value: T) -> Unit
) : ImmutableProperty<T>(name)