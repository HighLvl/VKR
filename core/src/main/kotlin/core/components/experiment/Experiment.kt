package core.components.experiment

import core.components.base.Component

interface Experiment: Component {
    val goals: Set<Goal>
    val observableVars: Map<String, GetterExp>
    val mutableVars: Map<String, SetterExp>
    val constraints: Set<Predicate>
}

typealias GetterExp = () -> Double
typealias SetterExp = (Double) -> Unit
typealias PredicateExp = () -> Boolean
typealias TargetFunction = () -> Double
data class Goal(val name: String, val targetFunction: TargetFunction)
data class Predicate(val name: String, val predicateExp: PredicateExp) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Predicate

        if (name != other.name) return false

        return true
    }

    override fun hashCode(): Int {
        return name.hashCode()
    }
}