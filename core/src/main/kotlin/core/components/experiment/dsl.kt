package core.components.experiment

import app.components.experiment.ExperimentTaskModel
import app.components.experiment.MutableExperimentTaskModel

class ExperimentTaskBuilder(private val model: MutableExperimentTaskModel) {
    fun goal(name: String, targetFunc: TargetFunction) = model.addGoal(name, targetFunc)
    fun setTargetScore(value: Double) = model.setTargetScore(value)
    fun stopOn(stopOnConditionBuilder: StopOnConditionBuilder.() -> Unit) = stopOnConditionBuilder(StopOnConditionBuilder(model))
    fun constraint(name: String, predicate: PredicateExp) = model.addConstraint(name, predicate)
    fun observableVariables(vararg variables: Pair<String, GetterExp>) = model.addObservableVariables(*variables)
    fun mutableVariables(vararg variables: Pair<String, SetterExp>) = model.addMutableVariables(*variables)
    fun observableVariable(name: String, getter: GetterExp) = model.addObservableVariables(name to getter)
    fun mutableVariable(name: String, setter: SetterExp) = model.addMutableVariables(name to setter)
}

class StopOnConditionBuilder(private val model: MutableExperimentTaskModel) {
    fun condition(name: String = "", predicate: PredicateExp) = model.addStopOnCondition(name, predicate)
    fun timeGreaterOrEqualsTo(value: Double) = model.setConditionStopOnTimeMoreThan(value)
}

fun experimentTask(buildTask: ExperimentTaskBuilder.() -> Unit): ExperimentTaskModel {
    val model = MutableExperimentTaskModel()
    ExperimentTaskBuilder(model).buildTask()
    return model
}