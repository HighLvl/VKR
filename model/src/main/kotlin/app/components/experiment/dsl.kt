package app.components.experiment

class ExperimentTaskContext(private val model: ExperimentTaskModel) {
    fun goal(score: Int, name: String, predicate: () -> Boolean) =
        model.addTargetFunc(score, name, predicate)
    fun stopOn(stopOnContext: StopOnContext.() -> Unit) = stopOnContext(StopOnContext(model))
    fun constraint(name: String, predicate: () -> Boolean) = model.addConstraint(name, predicate)
    fun observableVariables(vararg variables: Pair<String, () -> Float>) = model.addObservableVariables(*variables)
    fun mutableVariables(vararg variables: Pair<String, (Float) -> Unit>) = model.addMutableVariables(*variables)
}

class StopOnContext(private val model: ExperimentTaskModel) {
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

fun experimentTask(buildTask: ExperimentTaskContext.() -> Unit): ExperimentTaskModel {
    val model = ExperimentTaskModel()
    ExperimentTaskContext(model).buildTask()
    return model
}