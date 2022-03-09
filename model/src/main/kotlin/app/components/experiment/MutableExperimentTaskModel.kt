package app.components.experiment

abstract class ExperimentTaskModel {
    abstract val goals: Set<Goal>
    abstract val stopConditions: Set<Predicate>
    abstract val constraints: Set<Predicate>
    abstract val observableVariables: Map<String, () -> Float>
    abstract val mutableVariables: Map<String, (Float) -> Unit>

    @set:JvmName("setTargetScore1")
    var targetScore = Int.MAX_VALUE
        protected set
    var stopTime = Float.MAX_VALUE
        protected set

    data class Goal(val score: Int, val predicate: Predicate)
    data class Predicate(val name: String, val predicateFun: () -> Boolean) {
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
}

class MutableExperimentTaskModel: ExperimentTaskModel() {
    override val goals: Set<Goal>
        get() = _goals
    override val stopConditions: Set<Predicate>
        get() = _stopConditions
    override val constraints: Set<Predicate>
        get() = _constraints
    override val observableVariables: Map<String, () -> Float>
        get() = _observableVariables
    override val mutableVariables: Map<String, (Float) -> Unit>
        get() = _mutableVariables

    private val _observableVariables = mutableMapOf<String, () -> Float>()
    private val _mutableVariables = mutableMapOf<String, (Float) -> Unit>()
    private val _goals = mutableSetOf<Goal>()
    private val _stopConditions = mutableSetOf<Predicate>()
    private val _constraints = mutableSetOf<Predicate>()

    fun addGoal(score: Int, name: String = "", predicate: () -> Boolean) {
        _goals += Goal(score, Predicate(name, predicate))
    }

    fun addStopOnCondition(name: String = "", predicate: () -> Boolean) {
        _stopConditions += Predicate(name, predicate)
    }

    fun setTargetScore(score: Int) {
        targetScore = score
    }

    fun setConditionStopOnTimeMoreThan(stopTime: Float) {
        this.stopTime = stopTime
    }

    fun addConstraint(name: String = "", predicate: () -> Boolean) {
        _constraints += Predicate(name, predicate)
    }

    fun addObservableVariables(vararg variables: Pair<String, () -> Float>) = _observableVariables.putAll(variables)
    fun addMutableVariables(vararg variables: Pair<String, (Float) -> Unit>) = _mutableVariables.putAll(variables)
}

//fun main() {
//    val task = experimentTask {
//        goal(10, "some Func") {
//            4 < 100
//        }
//        constraint("one more than twelve") {
//            1 > 12
//        }
//        constraint("twelve more than fifteen") {
//            3 > 15
//        }
//        stopOn {
//            condition { 2 + 5 > 10 }
//            scoreMoreThan(6)
//            timeMoreThan(9f)
//        }
//        observableVariables(
//            "x" to { 1f },
//            "y" to { 2f }
//        )
//        mutableVariables("x" to {})
//    }
//    println()
//}


