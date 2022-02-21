package app.components.optimization
typealias MutableVariable = Pair<String, (Float) -> Unit>
typealias ObservableVariable = Pair<String, () -> Float>

class OptimizationTaskModel {
    val targetFuncList: List<TargetFunc>
        get() = _targetFuncList

    val stopConditionList: List<Predicate>
        get() = _stopConditionList

    val constraintList: List<Predicate>
        get() = _constraintList

    val observableVariables: List<ObservableVariable>
        get() = _observableVariables
    private val _observableVariables = mutableListOf<ObservableVariable>()
    val mutableVariables: List<MutableVariable>
        get() = _mutableVariables
    private val _mutableVariables = mutableListOf<MutableVariable>()


    var stopScore = Int.MAX_VALUE
        private set
    var stopTime = Float.MAX_VALUE
        private set

    private val _targetFuncList = mutableListOf<TargetFunc>()
    private val _stopConditionList = mutableListOf<Predicate>()
    private val _constraintList = mutableListOf<Predicate>()

    fun addTargetFunc(score: Int, name: String = "", predicate: () -> Boolean) {
        _targetFuncList += TargetFunc(score, name, predicate)
    }

    fun addStopOnCondition(name: String = "", predicate: () -> Boolean) {
        _stopConditionList += Predicate(name, predicate)
    }

    fun setConditionStopOnScoreMoreThan(score: Int) {
        stopScore = score
    }

    fun setConditionStopOnTimeMoreThan(stopTime: Float) {
        this.stopTime = stopTime
    }

    fun addConstraint(name: String = "", predicate: () -> Boolean) {
        _constraintList += Predicate(name, predicate)
    }

    fun addObservableVariables(vararg variables: ObservableVariable) = _observableVariables.addAll(variables)
    fun addMutableVariables(vararg variables: MutableVariable) = _mutableVariables.addAll(variables)

    class TargetFunc(val score: Int, val name: String, val predicate: () -> Boolean)
    class Predicate(val name: String, val predicate: () -> Boolean)
}

class OptimizationTaskContext(private val model: OptimizationTaskModel) {
    fun targetFunc(score: Int, name: String = "", predicate: () -> Boolean) = model.addTargetFunc(score, name, predicate)
    fun stopOn(stopOnContext: StopOnContext.() -> Unit) = stopOnContext(StopOnContext(model))
    fun constraint(name: String = "", predicate: () -> Boolean) = model.addConstraint(name, predicate)
    fun observableVariables(vararg variables: ObservableVariable) = model.addObservableVariables(*variables)
    fun mutableVariables(vararg variables: MutableVariable) = model.addMutableVariables(*variables)
}

class StopOnContext(private val model: OptimizationTaskModel) {
    fun condition(name: String = "", predicate: () -> Boolean) {
        model.addStopOnCondition(name, predicate)
    }

    fun scoreMoreThan(value: Int) {
        model.setConditionStopOnScoreMoreThan(value)
    }

    fun timeMoreThan(value: Float) {
        model.setConditionStopOnTimeMoreThan(value)
    }
}
fun optimizationTask(buildTask: OptimizationTaskContext.() -> Unit): OptimizationTaskModel {
    val model = OptimizationTaskModel()
    OptimizationTaskContext(model).buildTask()
    return model
}

fun main() {
    optimizationTask {
        targetFunc(10, "some Func") {
            4 < 100
        }
        constraint {
            1 > 12
        }
        constraint {
            3 > 15
        }
        stopOn {
            condition { 2 + 5 > 10 }
            scoreMoreThan(6)
            timeMoreThan(9f)
        }
        observableVariables(
            "x" to {1f},
            "y" to {2f}
        )
        mutableVariables("x" to {})
    }
}


