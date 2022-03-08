package view

import viewmodel.SceneViewModel
import widgets.Widget

class ScriptViewPortView(private val sceneViewModel: SceneViewModel): View(), Widget {
    override fun draw() {
        sceneViewModel.updateScriptsUI()
    }
}