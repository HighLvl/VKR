package views

class ScriptViewPort: View {
    var onDrawListener: () -> Unit = {}
    override fun draw() {
        onDrawListener()
    }
}