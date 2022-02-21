package app.components.optimization

class OptimizationTaskContext(private val model: OptimizationTaskModel) {
    fun targetFunc(score: Int, name: String = "", predicate: () -> Boolean) =
        model.addTargetFunc(score, name, predicate)

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