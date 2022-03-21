package app.components.experiment

import core.components.experiment.*

abstract class ExperimentTaskModel {
    abstract val goals: Set<Goal>
    abstract val stopConditions: Set<Predicate>
    abstract val constraints: Set<Predicate>
    abstract val observableVariables: Map<String, GetterExp>
    abstract val mutableVariables: Map<String, SetterExp>

    @set:JvmName("setTargetScore1")
    var targetScore = Double.MAX_VALUE
        protected set
    var stopTime = Double.MAX_VALUE
        protected set
}

class MutableExperimentTaskModel: ExperimentTaskModel() {
    override val goals: Set<Goal>
        get() = _goals
    override val stopConditions: Set<Predicate>
        get() = _stopConditions
    override val constraints: Set<Predicate>
        get() = _constraints
    override val observableVariables: Map<String, GetterExp>
        get() = _observableVariables
    override val mutableVariables: Map<String, SetterExp>
        get() = _mutableVariables

    private val _observableVariables = mutableMapOf<String, GetterExp>()
    private val _mutableVariables = mutableMapOf<String, SetterExp>()
    private val _goals = mutableSetOf<Goal>()
    private val _stopConditions = mutableSetOf<Predicate>()
    private val _constraints = mutableSetOf<Predicate>()

    fun addGoal(name: String, targetFunc: TargetFunction) {
        _goals += Goal(name, targetFunc)
    }

    fun addStopOnCondition(name: String = "", predicate: PredicateExp) {
        _stopConditions += Predicate(name, predicate)
    }

    fun setTargetScore(score: Double) {
        targetScore = score
    }

    fun setConditionStopOnTimeMoreThan(stopTime: Double) {
        this.stopTime = stopTime
    }

    fun addConstraint(name: String = "", predicate: PredicateExp) {
        _constraints += Predicate(name, predicate)
    }

    fun addObservableVariables(vararg variables: Pair<String, GetterExp>) = _observableVariables.putAll(variables)
    fun addMutableVariables(vararg variables: Pair<String, SetterExp>) = _mutableVariables.putAll(variables)
}