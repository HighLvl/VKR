package app.components.system.experiment.variables

import app.components.system.experiment.variables.mutable.MutableVariablesController
import app.components.system.experiment.variables.observable.ObservableVariablesController
import core.components.base.AddInSnapshot
import core.components.base.Component
import core.components.base.Script
import core.components.base.TargetEntity
import core.components.experiment.Experiment
import core.components.experiment.ExperimentTaskModel
import core.components.experiment.TrackedData
import core.entities.Experimenter
import core.entities.getComponent
import core.services.Services
import core.services.modelTime
import core.utils.Disposable

@TargetEntity(Experimenter::class)
class Variables : Component, Script {
    private val observableVariablesController = ObservableVariablesController()
    private val mutableVariablesController = MutableVariablesController()
    private val experiment = Services.scene.experimenter.getComponent<Experiment>()!!
    private val trackedData = Services.scene.experimenter.getComponent<TrackedData>()!!


    @AddInSnapshot(3)
    var showObservableVariables by observableVariablesController::enabled

    @AddInSnapshot(4)
    var showMutableVariables by mutableVariablesController::enabled
    private val disposables = mutableListOf<Disposable>()
    private lateinit var taskModel: ExperimentTaskModel

    override fun onAttach() {
        disposables += trackedData.trackedDataSizeObservable.observe { value ->
            observableVariablesController.trackedDataSize = value
            mutableVariablesController.trackedDataSize = value
        }
        disposables += trackedData.clearTrackedDataObservable.observe { reset() }
        disposables += experiment.taskModelObservable.observe {
            taskModel = it
            reset()
        }
    }

    override fun onDetach() {
        disposables.forEach { it.dispose() }
    }

    override fun onModelUpdate() {
        mutableVariablesController.onModelUpdate(modelTime)
    }

    override fun onModelAfterUpdate() {
        observableVariablesController.onModelUpdate(modelTime)
    }

    override fun updateUI() {
        observableVariablesController.update()
        mutableVariablesController.update()
    }

    private fun reset() {
        observableVariablesController.reset(taskModel.observableVariables)
        mutableVariablesController.reset(taskModel.mutableVariables)
    }
}