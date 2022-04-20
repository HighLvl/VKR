package core.components.base

abstract class Script: Component {
    protected open fun onModelRun() {}
    protected open fun onModelUpdate() {}
    protected open fun onModelAfterUpdate() {}
    protected open fun onModelStop() {}
    protected open fun updateUI() {}
    protected open fun onModelPause() {}
    protected open fun onModelResume() {}
    protected open fun onAttach() {}
    protected open fun onDetach() {}
}