package controllers

class SceneSetup {
    var onLoadConfigurationListener: (String) -> Unit = {}
    var onClearSceneListener: () -> Unit = {}
}