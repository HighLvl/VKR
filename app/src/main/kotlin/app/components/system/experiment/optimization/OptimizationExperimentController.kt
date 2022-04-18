package app.components.system.experiment.optimization

class OptimizationExperimentController {
    private val model = OptimizationExperimentModel()

    val isValidArgsObservable by model::isValidArgsObservable
    var taskModel by model::taskModel
    var inputParams by model::inputParams
    val commandObservable by model::commandObservable
    val ctrlMakeDecisionDataFlow by model::ctrlMakeDecisionDataFlow

    private val view = OptimizationExperimentView(model)

    fun stop() {
        model.stop()
    }

    fun start() {
        model.start()
    }

    fun onModelStop() {
        model.onModelStop()
    }

    fun onModelUpdate() {
        model.onModelUpdate()
    }

    fun update() {
        view.update()
    }
}