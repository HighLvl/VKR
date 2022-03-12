package app.components.experiment

import core.components.experiment.GetterExp
import core.components.experiment.PredicateExp
import core.components.experiment.SetterExp

class ExperimentTaskContext(private val model: MutableExperimentTaskModel) {
    fun goal(score: Int, name: String, predicate: PredicateExp) = model.addGoal(score, name, predicate)
    fun setTargetScore(value: Int) = model.setTargetScore(value)
    fun stopOn(stopOnContext: StopOnContext.() -> Unit) = stopOnContext(StopOnContext(model))
    fun constraint(name: String, predicate: PredicateExp) = model.addConstraint(name, predicate)
    fun observableVariables(vararg variables: Pair<String, GetterExp>) = model.addObservableVariables(*variables)
    fun mutableVariables(vararg variables: Pair<String, SetterExp>) = model.addMutableVariables(*variables)
    fun observableVariable(name: String, getter: GetterExp) = model.addObservableVariables(name to getter)
    fun mutableVariable(name: String, setter: SetterExp) = model.addMutableVariables(name to setter)
}

class StopOnContext(private val model: MutableExperimentTaskModel) {
    fun condition(name: String = "", predicate: PredicateExp) = model.addStopOnCondition(name, predicate)
    fun timeGreaterOrEqualsTo(value: Float) = model.setConditionStopOnTimeMoreThan(value)
}

fun experimentTask(buildTask: ExperimentTaskContext.() -> Unit): ExperimentTaskModel {
    val model = MutableExperimentTaskModel()
    ExperimentTaskContext(model).buildTask()
    return model
}