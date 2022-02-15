package views

class ScriptViewPort(private val doOnDraw: () -> Unit): View {
    override fun draw() {
        doOnDraw()
    }
}