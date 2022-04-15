package core.components.experiment

abstract class ExperimentTaskModel {
    abstract val inputParams: Set<InputParam>
    abstract val makeDecisionConditions: Map<String, PredicateExp>
    abstract val targetFunctionVH: ValueHolder<Double>
    abstract val goals: Set<Goal>
    abstract val stopConditions: Map<String, PredicateExp>
    abstract val observableVariables: Map<String, GetterExp>
    abstract val mutableVariables: Map<String, SetterExp>
    abstract val onBeginListeners: List<() -> Unit>
    abstract val onUpdateListeners: List<() -> Unit>
    abstract val onEndListeners: List<() -> Unit>
    abstract val onModelRunListener: () -> Unit
    abstract val onModelUpdateListener: () -> Unit
    abstract val onModelStopListener: () -> Unit
    abstract val onStartOptimizationListeners: List<() -> Unit>
    abstract val onStopOptimizationListeners: List<() -> Unit>

    @set:JvmName("setTargetScore1")
    var targetScore = 0
        protected set
}

data class MutableValueHolder<T>(override var value: T) : ValueHolder<T>

class MutableExperimentTaskModel: ExperimentTaskModel() {
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
    override val onBeginListeners: List<() -> Unit>
        get() = _onBeginListeners
    override val onUpdateListeners: List<() -> Unit>
        get() = _onUpdateListeners
    override val onEndListeners: List<() -> Unit>
        get() = _onEndListeners
    override var onModelRunListener: () -> Unit = {}
    override var onModelUpdateListener: () -> Unit = {}
    override var onModelStopListener: () -> Unit = {}
    override val onStartOptimizationListeners: List<() -> Unit>
        get() = _onStartOptimizationListeners
    override val onStopOptimizationListeners: List<() -> Unit>
        get() = _onStopOptimizationListeners

    private val _observableVariables = mutableMapOf<String, GetterExp>()
    private val _mutableVariables = mutableMapOf<String, SetterExp>()
    private val _makeDecisionConditions = mutableMapOf<String, PredicateExp>()
    private val _stopConditions = mutableMapOf<String, PredicateExp>()
    private val _goals = mutableSetOf<Goal>()
    private val _inputParams = mutableSetOf<InputParam>()
    private val _onBeginListeners = mutableListOf<() -> Unit>()
    private val _onUpdateListeners = mutableListOf<() -> Unit>()
    private val _onEndListeners = mutableListOf<() -> Unit>()
    private val _onStartOptimizationListeners = mutableListOf<() -> Unit>()
    private val _onStopOptimizationListeners = mutableListOf<() -> Unit>()

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

    fun addOnStopOptimizationListener(listener: () -> Unit) {
        _onStopOptimizationListeners += listener
    }

    fun addOnStartOptimizationListener(listener: () -> Unit) {
        _onStartOptimizationListeners += listener
    }
}