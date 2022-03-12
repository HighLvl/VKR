package core.components.experiment

import core.components.SystemComponent
interface Experiment: SystemComponent {
    val goals: Set<Goal>
    val observableVars: Map<String, GetterExp>
    val mutableVars: Map<String, SetterExp>
    val constraints: Set<Predicate>
}

typealias GetterExp = () -> Float
typealias SetterExp = (Float) -> Unit
typealias PredicateExp = () -> Boolean
data class Goal(val score: Int, val predicate: Predicate)
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