package core.components.experiment

@DslMarker
annotation class ExperimentDslMarker

@ExperimentDslMarker
class ExperimentTaskBuilder(private val model: MutableExperimentTaskModel) {

    fun optimization(targetScore: Double, builder: OptimizationBuilder.() -> Unit) {
        model.setTargetScore(targetScore)
        builder(OptimizationBuilder(model))
    }

    fun variables(builder: VariablesBuilder.() -> Unit) {
        VariablesBuilder(model).apply(builder)
    }
}

@ExperimentDslMarker
class OptimizationBuilder(private val model: MutableExperimentTaskModel) {
    fun inputParams(inputParamsBuilder: InputParamsBuilder.() -> Unit) = inputParamsBuilder(InputParamsBuilder(model))
    fun goals(goalsBuilder: GoalsBuilder.() -> Unit) = goalsBuilder(GoalsBuilder(model))
    fun constraints(constraintsBuilder: ConstraintsBuilder.() -> Unit) = constraintsBuilder(ConstraintsBuilder(model))
    fun stopOn(stopOnConditionBuilder: StopOnConditionBuilder.() -> Unit) =
        stopOnConditionBuilder(StopOnConditionBuilder(model))
}

@ExperimentDslMarker
class InputParamsBuilder(private val model: MutableExperimentTaskModel) {
    fun param(name: String, initialValue: Double, setter: SetterExp) =
        model.addInputParam(InputParam(name, initialValue, setter))
}

@ExperimentDslMarker
class GoalsBuilder(private val model: MutableExperimentTaskModel) {
    fun goal(name: String, rating: Double, targetFunc: TargetFunction) = model.addGoal(name, rating, targetFunc)
}

@ExperimentDslMarker
class ConstraintsBuilder(private val model: MutableExperimentTaskModel) {
    fun constraint(name: String, predicate: PredicateExp) = model.addConstraint(name, predicate)
}

@ExperimentDslMarker
class VariablesBuilder(private val model: MutableExperimentTaskModel) {
    fun observable(name: String, getter: GetterExp) = model.addObservableVariables(name to getter)
    fun mutable(name: String, setter: SetterExp) = model.addMutableVariables(name to setter)
}

@ExperimentDslMarker
class StopOnConditionBuilder(private val model: MutableExperimentTaskModel) {
    fun condition(name: String, predicate: PredicateExp) = model.addStopOnCondition(name, predicate)
    fun timeGreaterOrEqualsTo(value: Double) = model.setConditionStopOnTimeMoreThan(value)
}

@ExperimentDslMarker
fun experimentTask(buildTask: ExperimentTaskBuilder.() -> Unit): ExperimentTaskModel {
    val model = MutableExperimentTaskModel()
    ExperimentTaskBuilder(model).buildTask()
    return model
}