package core.components.experiment

import core.services.dt
import core.services.modelTime

@DslMarker
annotation class ExperimentDslMarker

@ExperimentDslMarker
class ExperimentTaskBuilder(private val model: MutableExperimentTaskModel) {
    fun modelLifecycle(builder: ModelLifecycleListenerBuilder.() -> Unit) = builder(ModelLifecycleListenerBuilder(model))

    fun optimization(targetScore: Double, builder: OptimizationBuilder.() -> Unit) {
        model.setTargetScore(targetScore)
        builder(OptimizationBuilder(model))
    }

    fun variables(builder: VariablesBuilder.() -> Unit) {
        VariablesBuilder(model).apply(builder)
    }
}

@ExperimentDslMarker
class ModelLifecycleListenerBuilder(private val model: MutableExperimentTaskModel) {
    fun onRun(block: () -> Unit) {
        model.onModelRunListener = block
    }

    fun onUpdate(block: () -> Unit) {
        model.onModelUpdateListener = block
    }

    fun onStop(block: () -> Unit) {
        model.onModelStopListener = block
    }
}

@ExperimentDslMarker
class OptimizationBuilder(private val model: MutableExperimentTaskModel) {
    fun inputParams(inputParamsBuilder: InputParamsBuilder.() -> Unit) = inputParamsBuilder(InputParamsBuilder(model))
    fun goals(goalsBuilder: GoalsBuilder.() -> Unit) = goalsBuilder(GoalsBuilder(model))
    fun constraints(constraintsBuilder: ConstraintsBuilder.() -> Unit) = constraintsBuilder(ConstraintsBuilder(model))
    /** Условия необходимости принятия решения */
    fun makeDecisionOn(makeDecisionOnConditionBuilder: MakeDecisionOnConditionBuilder.() -> Unit) =
        makeDecisionOnConditionBuilder(MakeDecisionOnConditionBuilder(model))
    /** Условия завершения оптимизации */
    fun stopOn(stopOnConditionBuilder: StopOnConditionBuilder.() -> Unit) =
        stopOnConditionBuilder(StopOnConditionBuilder(model))
}

@ExperimentDslMarker
class InputParamsBuilder(private val model: MutableExperimentTaskModel) {
    fun param(name: String, initialValue: Double, setter: SetterExp) =
        model.addInputParam(InputParam(name, initialValue, setter))
}

@ExperimentDslMarker
class GoalsBuilder(private val model: MutableExperimentTaskModel) : OptimizerLifecycleEventsListener(model) {
    /** Итоговое значение эквивалентно последнему мгновенному значению целевой функции
     * @see ValueHolder
     */
    fun lastInstant(name: String, rating: Double, getInstantValue: TargetFunction) {
        model.addGoal(name, rating, MutableValueHolder(0.0, 0.0).apply {
            begin {
                instantValue = 0.0
                value = 0.0
            }

            update {
                instantValue = getInstantValue()
            }

            end {
                value = instantValue
            }
        })
    }

    /** Итоговое значение эквивалентно математическому ожиданию мгновенных значений целевой функции
     * @param getInstantValue геттер мгновенного значения целевой функции
     */
    fun expectedValue(name: String, rating: Double, getInstantValue: TargetFunction) {
        model.addGoal(name, rating, MutableValueHolder(0.0, 0.0).apply {
            var sum = 0.0
            var startTime = 0.0
            //init
            begin {
                sum = 0.0
                startTime = modelTime
            }
            //calculate instant value and sum
            update {
                instantValue = getInstantValue()
                sum += instantValue * dt
            }
            //calculate ev
            end {
                value = sum / (modelTime - startTime)
            }
        })

    }

    /** Построение цели с пользовательским вычислением мгновенного и итогового значений
     */
    fun custom(name: String, rating: Double, targetFuncBuilder: MutableValueHolder<Double>.() -> Unit) {
        model.addGoal(name, rating, MutableValueHolder(0.0, 0.0).apply(targetFuncBuilder))
    }
}

@ExperimentDslMarker
class ConstraintsBuilder(private val model: MutableExperimentTaskModel) : OptimizerLifecycleEventsListener(model) {
    /** Ограничение соблюдено, если последнее мгновенное значение предиката истинно. (value == instantValue)
     * @see ValueHolder
     */
    fun lastInstant(name: String, getInstantValue: PredicateExp) {
        model.addConstraint(name, MutableValueHolder(value = false, instantValue = false).apply {
            begin {
                instantValue = false
                value = false
            }
            update {
                instantValue = getInstantValue()
            }
            end {
                value = instantValue
            }
        })
    }

    /** Ограничение соблюдено, если все мгновенные значения предиката истинны
     */
    fun allInstant(name: String, getInstantValue: PredicateExp) {
        model.addConstraint(name, MutableValueHolder(value = false, instantValue = false).apply {
            begin {
                instantValue = true
                value = true
            }
            update {
                instantValue = getInstantValue()
                value = value && instantValue
            }
        })
    }

    /** Построение ограничения с пользовательским вычислением мгновенного и итогового значений
     */
    fun custom(name: String, predicateBuilder: MutableValueHolder<Boolean>.() -> Unit) {
        model.addConstraint(name, MutableValueHolder(value = false, instantValue = false).apply(predicateBuilder))
    }
}

@ExperimentDslMarker
class VariablesBuilder(private val model: MutableExperimentTaskModel) {
    fun observable(name: String, getter: GetterExp) = model.addObservableVariables(name to getter)
    fun mutable(name: String, setter: SetterExp) = model.addMutableVariables(name to setter)
}

@ExperimentDslMarker
class StopOnConditionBuilder(private val model: MutableExperimentTaskModel) : OptimizerLifecycleEventsListener(model) {
    fun condition(name: String, predicate: PredicateExp) = model.addStopOnCondition(name, predicate)
    fun modelTime(t: Double) = model.addStopOnCondition("Model Time >= $t") { modelTime >= t }
}

@ExperimentDslMarker
fun experimentTask(buildTask: ExperimentTaskBuilder.() -> Unit): ExperimentTaskModel {
    val model = MutableExperimentTaskModel()
    ExperimentTaskBuilder(model).buildTask()
    return model
}

abstract class OptimizerLifecycleEventsListener(private val model: MutableExperimentTaskModel) {
    /** Вызывается в начале процесса вычисления данных, используемых для принятия оптимизационного решения.
     * Используется для инициализации переменных
     */
    fun begin(listener: () -> Unit) {
        model.addOnBeginListener(listener)
    }

    /** Вызывается на получении снимка состояния модели.
     * Используется для обновления переменных
     */
    fun update(listener: () -> Unit) {
        model.addOnUpdateListener(listener)
    }

    /** Вызывается перед началом процесса принятия решения.
     * Используется для завершения вычислений, используемых для принятия оптимизационного решения.
     */
    fun end(listener: () -> Unit) {
        model.addOnEndListener(listener)
    }
}

/** Решение принимается в случае, если выполнено как минимум одно из условий.
 *  приостановка модели -> планировка запросов к модели -> возобновление модели
 */
@ExperimentDslMarker
class MakeDecisionOnConditionBuilder(private val model: MutableExperimentTaskModel) :
    OptimizerLifecycleEventsListener(model) {
    fun condition(name: String, predicate: PredicateExp) = model.addMakeDecisionOnCondition(name, predicate)
    fun timeSinceLastDecision(t: Double) {
        var startTime = 0.0
        begin {
            startTime = modelTime
        }
        model.addMakeDecisionOnCondition("Time since last decision >= $t") { modelTime >= startTime + t }
    }
}