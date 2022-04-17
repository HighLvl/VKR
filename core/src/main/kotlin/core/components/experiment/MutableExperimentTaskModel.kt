package core.components.experiment

import core.utils.EmptyPublishSubject

abstract class ExperimentTaskModel {
    abstract val inputParams: Set<InputParam>
    abstract val constraint: (Map<String, Double>) -> Boolean
    abstract val makeDecisionConditions: Map<String, PredicateExp>
    abstract val targetFunctionVH: ValueHolder<Double>
    abstract val goals: Set<Goal>
    abstract val stopConditions: Map<String, PredicateExp>
    abstract val observableVariables: Map<String, GetterExp>
    abstract val mutableVariables: Map<String, SetterExp>
    val beginObservable = EmptyPublishSubject()
    val updateObservable = EmptyPublishSubject()
    val endObservable = EmptyPublishSubject()
    val modelRunObservable = EmptyPublishSubject()
    val modelUpdateObservable = EmptyPublishSubject()
    val modelStopObservable = EmptyPublishSubject()
    val startOptimizationObservable = EmptyPublishSubject()
    val stopOptimizationObservable = EmptyPublishSubject()

    @set:JvmName("setTargetScore1")
    var targetScore = 0
        protected set
}

data class MutableValueHolder<T>(override var value: T) : ValueHolder<T>

class MutableExperimentTaskModel : ExperimentTaskModel() {
    override val makeDecisionConditions: Map<String, PredicateExp>
        get() = _makeDecisionConditions
    override var targetFunctionVH: ValueHolder<Double> = MutableValueHolder(0.0)
    override val stopConditions: Map<String, PredicateExp>
        get() = _stopConditions
    override val goals: Set<Goal>
        get() = _goals
    override val observableVariables: Map<String, GetterExp>
        get() = _observableVariables
    override val mutableVariables: Map<String, SetterExp>
        get() = _mutableVariables
    override val inputParams: Set<InputParam>
        get() = _inputParams
    override var constraint: (Map<String, Double>) -> Boolean = { true }

    private val _observableVariables = mutableMapOf<String, GetterExp>()
    private val _mutableVariables = mutableMapOf<String, SetterExp>()
    private val _makeDecisionConditions = mutableMapOf<String, PredicateExp>()
    private val _stopConditions = mutableMapOf<String, PredicateExp>()
    private val _goals = mutableSetOf<Goal>()
    private val _inputParams = mutableSetOf<InputParam>()

    fun addStopOnCondition(name: String, predicate: PredicateExp) {
        _stopConditions[name] = predicate
    }

    fun setTargetScore(score: Int) {
        targetScore = score
    }

    fun addGoal(name: String, score: Int, predicate: ValueHolder<Boolean>) {
        _goals += Goal(name, score, predicate)
    }

    fun addObservableVariables(vararg variables: Pair<String, GetterExp>) = _observableVariables.putAll(variables)
    fun addMutableVariables(vararg variables: Pair<String, SetterExp>) = _mutableVariables.putAll(variables)

    fun addInputParam(inputParam: InputParam) {
        _inputParams.add(inputParam)
    }

    fun addMakeDecisionOnCondition(name: String, predicate: PredicateExp /* = () -> kotlin.Boolean */) {
        _makeDecisionConditions[name] = predicate
    }

}