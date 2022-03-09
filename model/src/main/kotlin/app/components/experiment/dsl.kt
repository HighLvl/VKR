package app.components.experiment

class ExperimentTaskContext(private val model: MutableExperimentTaskModel) {
    fun goal(score: Int, name: String, predicate: () -> Boolean) = model.addGoal(score, name, predicate)
    fun setTargetScore(value: Int) = model.setTargetScore(value)
    fun stopOn(stopOnContext: StopOnContext.() -> Unit) = stopOnContext(StopOnContext(model))
    fun constraint(name: String, predicate: () -> Boolean) = model.addConstraint(name, predicate)
    fun observableVariables(vararg variables: Pair<String, () -> Float>) = model.addObservableVariables(*variables)
    fun mutableVariables(vararg variables: Pair<String, (Float) -> Unit>) = model.addMutableVariables(*variables)
    fun observableVariable(name: String, getter: () -> Float) = model.addObservableVariables(name to getter)
    fun mutableVariable(name: String, setter: (Float) -> Unit) = model.addMutableVariables(name to setter)
}

class StopOnContext(private val model: MutableExperimentTaskModel) {
    fun condition(name: String = "", predicate: () -> Boolean) = model.addStopOnCondition(name, predicate)
    fun timeGreaterOrEqualsTo(value: Float) = model.setConditionStopOnTimeMoreThan(value)
}

fun experimentTask(buildTask: ExperimentTaskContext.() -> Unit): ExperimentTaskModel {
    val model = MutableExperimentTaskModel()
    ExperimentTaskContext(model).buildTask()
    return model
}