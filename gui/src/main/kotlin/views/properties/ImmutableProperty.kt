package views.properties

abstract class ImmutableProperty<T>(name: String) : Property(name) {
    abstract var value: T
}