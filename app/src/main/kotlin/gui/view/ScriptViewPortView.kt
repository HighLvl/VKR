package gui.view

import gui.viewmodel.SceneViewModel
import gui.widgets.Widget

class ScriptViewPortView(private val sceneViewModel: SceneViewModel): View(), Widget {
    override fun draw() {
        sceneViewModel.updateScriptsUI()
    }
}