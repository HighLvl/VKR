package core.components.experiment

abstract class ExperimentTaskModel {
    abstract val inputParams: Set<InputParam>
    abstract val makeDecisionConditions: Map<String, PredicateExp>
    abstract val goals: Set<Goal>
    abstract val constraints: Set<Predicate>
    abstract val stopConditions: Map<String, PredicateExp>
    abstract val observableVariables: Map<String, GetterExp>
    abstract val mutableVariables: Map<String, SetterExp>
    abstract val onBeginListeners: List<() -> Unit>
    abstract val onUpdateListeners: List<() -> Unit>
    abstract val onEndListeners: List<() -> Unit>
    abstract val onModelRunListener: () -> Unit
    abstract val onModelUpdateListener: () -> Unit
    abstract val onModelStopListener: () -> Unit

    @set:JvmName("setTargetScore1")
    var targetScore = Double.MAX_VALUE
        protected set
}

data class MutableValueHolder<T>(override var value: T, override var instantValue: T) : ValueHolder<T>

class MutableExperimentTaskModel: ExperimentTaskModel() {
    override val makeDecisionConditions: Map<String, PredicateExp>
        get() = _makeDecisionConditions
    override val goals: Set<Goal>
        get() = _goals
    override val stopConditions: Map<String, PredicateExp>
        get() = _stopConditions
    override val constraints: Set<Predicate>
        get() = _constraints
    override val observableVariables: Map<String, GetterExp>
        get() = _observableVariables
    override val mutableVariables: Map<String, SetterExp>
        get() = _mutableVariables
    override val inputParams: Set<InputParam>
        get() = _inputParams
    override val onBeginListeners: List<() -> Unit>
        get() = _onBeginListeners
    override val onUpdateListeners: List<() -> Unit>
        get() = _onUpdateListeners
    override val onEndListeners: List<() -> Unit>
        get() = _onEndListeners
    override var onModelRunListener: () -> Unit = {}
    override var onModelUpdateListener: () -> Unit = {}
    override var onModelStopListener: () -> Unit = {}

    private val _observableVariables = mutableMapOf<String, GetterExp>()
    private val _mutableVariables = mutableMapOf<String, SetterExp>()
    private val _makeDecisionConditions = mutableMapOf<String, PredicateExp>()
    private val _goals = mutableSetOf<Goal>()
    private val _stopConditions = mutableMapOf<String, PredicateExp>()
    private val _constraints = mutableSetOf<Predicate>()
    private val _inputParams = mutableSetOf<InputParam>()
    private val _onBeginListeners = mutableListOf<() -> Unit>()
    private val _onUpdateListeners = mutableListOf<() -> Unit>()
    private val _onEndListeners = mutableListOf<() -> Unit>()

    fun addGoal(name: String, rating: Double, targetFunc: ValueHolder<Double>) {
        _goals += Goal(name, rating, targetFunc)
    }

    fun addStopOnCondition(name: String, predicate: PredicateExp) {
        _stopConditions[name] = predicate
    }

    fun setTargetScore(score: Double) {
        targetScore = score
    }

    fun addConstraint(name: String, predicate: ValueHolder<Boolean>) {
        _constraints += Predicate(name, predicate)
    }

    fun addObservableVariables(vararg variables: Pair<String, GetterExp>) = _observableVariables.putAll(variables)
    fun addMutableVariables(vararg variables: Pair<String, SetterExp>) = _mutableVariables.putAll(variables)

    fun addInputParam(inputParam: InputParam) {
        _inputParams.add(inputParam)
    }

    fun addOnBeginListener(listener: () -> Unit) {
        _onBeginListeners += listener
    }

    fun addOnUpdateListener(listener: () -> Unit) {
        _onUpdateListeners += listener
    }

    fun addOnEndListener(listener: () -> Unit) {
        _onEndListeners += listener
    }

    fun addMakeDecisionOnCondition(name: String, predicate: PredicateExp /* = () -> kotlin.Boolean */) {
        _makeDecisionConditions[name] = predicate
    }
}