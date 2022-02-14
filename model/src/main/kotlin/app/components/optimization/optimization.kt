package app.components.optimization//typealias ObservableVariable = Pair<String, () -> Float>
//typealias MutableVariable = Pair<String, (Float) -> Unit>
//
//
//class OptimizationTaskModel {
//    val targetFuncList: List<TargetFunc>
//        get() = _targetFuncList
//
//    val stopConditionList: List<Predicate>
//        get() = _stopConditionList
//
//    val constraintList: List<Predicate>
//        get() = _constraintList
//
//    val observableVariables: List<ObservableVariable>
//        get() = _observableVariables
//    private val _observableVariables = mutableListOf<ObservableVariable>()
//
//
//    var stopScore = Int.MAX_VALUE
//        private set
//    var stopTime = Float.MAX_VALUE
//        private set
//
//    private val _targetFuncList = mutableListOf<TargetFunc>()
//    private val _stopConditionList = mutableListOf<Predicate>()
//    private val _constraintList = mutableListOf<Predicate>()
//
//    fun addTargetFunc(score: Int, name: String = "", predicate: () -> Boolean) {
//        _targetFuncList += TargetFunc(score, name, predicate)
//    }
//
//    fun addStopOnCondition(name: String = "", predicate: () -> Boolean) {
//        _stopConditionList += Predicate(name, predicate)
//    }
//
//    fun setConditionStopOnScoreMoreThan(score: Int) {
//        stopScore = score
//    }
//
//    fun setConditionStopOnTimeMoreThan(stopTime: Float) {
//        this.stopTime = stopTime
//    }
//
//    fun addConstraint(name: String = "", predicate: () -> Boolean) {
//        _constraintList += Predicate(name, predicate)
//    }
//
//    fun addObservableVariables(vararg variables: ObservableVariable) = _observableVariables.addAll(variables)
//
//    class TargetFunc(val score: Int, val name: String, val predicate: () -> Boolean)
//    class Predicate(val name: String, val predicate: () -> Boolean)
//}
//
//
//class OptimizationTaskContext(private val model: OptimizationTaskModel) {
//    fun targetFunc(score: Int, name: String = "", predicate: () -> Boolean) =
//        model.addTargetFunc(score, name, predicate)
//
//    fun stopOn(onContext: StopOnContext.() -> Unit) {
//        onContext(StopOnContext(model))
//    }
//
//    fun constraint(name: String = "", predicate: () -> Boolean) =
//        model.addConstraint(name, predicate)
//
//    fun addObservableVariables(vararg variables: ObservableVariable) =
//        model.addObservableVariables(*variables)
//
//    fun addMutableVariables(vararg va)
//}
//
//class StopOnContext(private val model: OptimizationTaskModel) {
//    fun condition(name: String = "", predicate: () -> Boolean) {
//        model.addStopOnCondition(name, predicate)
//    }
//
//    fun scoreMoreThan(value: Int) {
//        model.setConditionStopOnScoreMoreThan(value)
//    }
//
//    infix fun timeMoreThan(value: Float) {
//        model.setConditionStopOnTimeMoreThan(value)
//    }
//}
//
//fun optimizationTask(onContext: OptimizationTaskContext.() -> Unit): OptimizationTaskModel {
//    val model = OptimizationTaskModel()
//    onContext(OptimizationTaskContext(model))
//    return model
//}
//
//val snap = Simple1Agent.Snapshot()
//
//fun main() {
//    optimizationTask {
//        targetFunc(10, "some Func") {
//            snap.x() * snap.y() < 100
//        }
//
//        constraint {
//            snap.x() > 12
//        }
//        constraint {
//            snap.y() > 15
//        }
//
//        stopOn {
//            condition { snap.x() + snap.y() > 10 }
//            scoreMoreThan(6)
//            timeMoreThan(9f)
//        }
//
//        addObservableVariables(
//            "x" to {1f},
//            "y" to {2f},
//            {3f}
//        )
//    }
//}
//
//
