package core.components.experiment

import core.services.dt
import core.services.modelTime

@DslMarker
annotation class ExperimentDslMarker

@ExperimentDslMarker
class ExperimentTaskBuilder(private val model: MutableExperimentTaskModel) {
    /** Подписка на события жизненного цикла модели.
     * Может быть использована для вычисления значений разделяемых переменных и вывода данных в файл
     */
    fun modelLifecycle(builder: ModelLifecycleListenerBuilder.() -> Unit) = builder(ModelLifecycleListenerBuilder(model))

    /** Конфигурация задания на оптимизационный эксперимент.
     * Процесс принятия оптимизационных решений начинается с запуска модели и проходит в рамках одного запуска.
     * В результате принятия оптимизационного решения изменяются входные параметры модели.
     * Оптимизационный эксперимент заканчивается, в случае достижения его цели или выполнения условий останова
     * процесса оптимизации.
     * @param targetScore целевая сумма рейтингов достигнутых целей
     * @see OptimizationBuilder.makeDecisionOn
     * @see OptimizationBuilder.inputParams
     * @see OptimizationBuilder.goals
     * @see OptimizationBuilder.constraints
     * @see OptimizationBuilder.stopOn
     */
    fun optimization(targetScore: Double, builder: OptimizationBuilder.() -> Unit) {
        model.setTargetScore(targetScore)
        builder(OptimizationBuilder(model))
    }

    /** Конфигурация наблюдаемых и изменяемых переменных.
     * Значения наблюдаемых переменных протоколируются на каждом снимке состояния модели.
     * Значения изменяемых переменных протоколируются после отправки запросов,
     * изменяющих значения переменных агентов модели, на сервер модели.
     */
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
    /** Параметры, изменяемые при принятии решения
     */
    fun inputParams(inputParamsBuilder: InputParamsBuilder.() -> Unit) = inputParamsBuilder(InputParamsBuilder(model))

    /** Цели оптимизации. Цель описывается с помощью 3-х элементов: имя, рейтинг, целевая функция.
     *  Если значение функции >= рейтинга, то цель считается достигнутой и вносит свой рейтинг в общую сумму.
     *  Для достижения цели оптимизационного эксперимента, сумма рейтингов должна быть >= целевой суммы
     *  и все ограничения должны быть соблюдены
     */
    fun goals(goalsBuilder: GoalsBuilder.() -> Unit) = goalsBuilder(GoalsBuilder(model))

    /** Ограничения
     */
    fun constraints(constraintsBuilder: ConstraintsBuilder.() -> Unit) = constraintsBuilder(ConstraintsBuilder(model))
    /** Условия необходимости принятия решения. Решение должно быть принято в случае, если выполнено как минимум одно из условий.
     * Для принятия решения используются итоговые значения целевых функций и ограничений
     * @see ValueHolder.value
     * @see ValueHolder.instantValue
     *  приостановка модели -> планировка запросов к модели -> возобновление модели
    */
    fun makeDecisionOn(makeDecisionOnConditionBuilder: MakeDecisionOnConditionBuilder.() -> Unit) =
        makeDecisionOnConditionBuilder(MakeDecisionOnConditionBuilder(model))
    /** Иные условия завершения оптимизационного эксперимента */
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
     * @see ValueHolder.instantValue
     * @see ValueHolder.value
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
    /**
     * @param predicate должен возвращать Boolean, свидетельствующий о необходимости остановки процесса оптимизации
     */
    fun condition(name: String, predicate: PredicateExp) = model.addStopOnCondition(name, predicate)

    /** modelTime >= t
     */
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

@ExperimentDslMarker
class MakeDecisionOnConditionBuilder(private val model: MutableExperimentTaskModel) :
    OptimizerLifecycleEventsListener(model) {
    /** Используется для создания пользовательских условий,
     *  при выполнении которых необходимо принять оптимизационное решение
     */
    fun condition(name: String, predicate: PredicateExp) = model.addMakeDecisionOnCondition(name, predicate)

    /** Используется для принятий решений через фиксированные промежутки времени t
     */
    fun timeSinceLastDecision(t: Double) {
        var startTime = 0.0
        begin {
            startTime = modelTime
        }
        model.addMakeDecisionOnCondition("Time since last decision >= $t") { modelTime >= startTime + t }
    }
}