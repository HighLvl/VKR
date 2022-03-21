package core.components.experiment

import app.components.experiment.ExperimentTaskModel
import app.components.experiment.MutableExperimentTaskModel

@DslMarker
annotation class ExperimentDslMarker

@ExperimentDslMarker
class ExperimentTaskBuilder(private val model: MutableExperimentTaskModel) {
    fun goal(name: String, targetFunc: TargetFunction) = model.addGoal(name, targetFunc)
    fun setTargetScore(value: Double) = model.setTargetScore(value)
    fun stopOn(stopOnConditionBuilder: StopOnConditionBuilder.() -> Unit) = stopOnConditionBuilder(StopOnConditionBuilder(model))
    fun constraint(name: String, predicate: PredicateExp) = model.addConstraint(name, predicate)
    fun variables(builder: VariablesBuilder.() -> Unit) {
        VariablesBuilder(model).apply(builder)
    }
}

@ExperimentDslMarker
class VariablesBuilder(private val model: MutableExperimentTaskModel) {
    fun observable(name: String, getter: GetterExp) = model.addObservableVariables(name to getter)
    fun mutable(name: String, setter: SetterExp) = model.addMutableVariables(name to setter)
}

@ExperimentDslMarker
class StopOnConditionBuilder(private val model: MutableExperimentTaskModel) {
    fun condition(name: String = "", predicate: PredicateExp) = model.addStopOnCondition(name, predicate)
    fun timeGreaterOrEqualsTo(value: Double) = model.setConditionStopOnTimeMoreThan(value)
}

@ExperimentDslMarker
fun experimentTask(buildTask: ExperimentTaskBuilder.() -> Unit): ExperimentTaskModel {
    val model = MutableExperimentTaskModel()
    ExperimentTaskBuilder(model).buildTask()
    return model
}