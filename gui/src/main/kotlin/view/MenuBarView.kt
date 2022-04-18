package view

import imgui.flag.ImGuiCol
import imgui.internal.ImGui
import imgui.type.ImBoolean
import utils.getString
import viewmodel.SceneViewModel
import widgets.Widget

class MenuBarView(private val sceneViewModel: SceneViewModel) : View(), Widget {
    private var _menuBarHeight: Float = 0f
    private var _menuBarWidth: Float = 0f
    private var _menuBarX: Float = 0f
    private var _menuBarY: Float = 0f
    private val isDarkTheme = ImBoolean(false)

    val height: Float
        get() = _menuBarHeight
    val width: Float
        get() = _menuBarWidth
    val posX: Float
        get() = _menuBarX
    val posY: Float
        get() = _menuBarY

    override fun onPreRun() {
        setupImGuiStyle(false)
    }

    override fun draw() {
        ImGui.beginMainMenuBar()
        _menuBarHeight = ImGui.getWindowHeight()
        _menuBarWidth = ImGui.getWindowWidth()
        val menuBarPos = ImGui.getWindowPos()
        _menuBarX = menuBarPos.x
        _menuBarY = menuBarPos.y
        if (ImGui.beginMenu(getString("menubar_view_title"))) {
            if (ImGui.checkbox(getString("menubar_view_dark_theme_option"), isDarkTheme)) {
                setupImGuiStyle(isDarkTheme.get())
            }
            ImGui.endMenu()
        }
        ImGui.endMainMenuBar()
    }

    private fun setupImGuiStyle(bStyleDark_: Boolean, alpha_: Float = 1.0f) {
        val style = ImGui.getStyle()

        // light style from Pacôme Danhiez (app.components.user itamago) https://github.com/ocornut/imgui/pull/511#issuecomment-175719267
        style.alpha = 1.0f
        style.frameRounding = 3.0f
        style.setColor(ImGuiCol.Text, 0.00f, 0.00f, 0.00f, 1.00f)
        style.setColor(ImGuiCol.TextDisabled, 0.60f, 0.60f, 0.60f, 1.00f)
        style.setColor(ImGuiCol.WindowBg, 0.94f, 0.94f, 0.94f, 0.94f)
        style.setColor(ImGuiCol.ChildBg, 0.00f, 0.00f, 0.00f, 0.00f)
        style.setColor(ImGuiCol.PopupBg, 0.9f, 0.9f, 0.9f, 0.94f)
        style.setColor(ImGuiCol.Border, 0.00f, 0.00f, 0.00f, 0.39f)
        style.setColor(ImGuiCol.BorderShadow, 1.00f, 1.00f, 1.00f, 0.10f)
        style.setColor(ImGuiCol.FrameBg, 1f, 1f, 1f, 0.94f)
        style.setColor(ImGuiCol.FrameBgHovered, 0.26f, 0.59f, 0.98f, 0.40f)
        style.setColor(ImGuiCol.FrameBgActive, 0.26f, 0.59f, 0.98f, 0.67f)
        style.setColor(ImGuiCol.TitleBg, 0.9f, 0.9f, 0.9f, 0.94f)
        style.setColor(ImGuiCol.TitleBgCollapsed, 1.00f, 1.00f, 1.00f, 0.51f)
        style.setColor(ImGuiCol.TitleBgActive, 0.9f, 0.9f, 0.9f, 0.94f)
        style.setColor(ImGuiCol.MenuBarBg, 0.9f, 0.9f, 0.9f, 0.94f)
        style.setColor(ImGuiCol.ScrollbarBg, 0.98f, 0.98f, 0.98f, 0.53f)
        style.setColor(ImGuiCol.ScrollbarGrab, 0.69f, 0.69f, 0.69f, 1.00f)
        style.setColor(ImGuiCol.ScrollbarGrabHovered, 0.59f, 0.59f, 0.59f, 1.00f)
        style.setColor(ImGuiCol.ScrollbarGrabActive, 0.49f, 0.49f, 0.49f, 1.00f)
        // style.setColor(ImGuiCol.ComboBg, 0.86f, 0.86f, 0.86f, 0.99f)
        style.setColor(ImGuiCol.CheckMark, 0.26f, 0.59f, 0.98f, 1.00f)
        style.setColor(ImGuiCol.SliderGrab, 0.24f, 0.52f, 0.88f, 1.00f)
        style.setColor(ImGuiCol.SliderGrabActive, 0.26f, 0.59f, 0.98f, 1.00f)
        style.setColor(ImGuiCol.Button, 0.26f, 0.59f, 0.98f, 0.40f)
        style.setColor(ImGuiCol.ButtonHovered, 0.26f, 0.59f, 0.98f, 1.00f)
        style.setColor(ImGuiCol.ButtonActive, 0.06f, 0.53f, 0.98f, 1.00f)
        style.setColor(ImGuiCol.Tab, 0.26f, 0.59f, 0.98f, 0.40f)
        style.setColor(ImGuiCol.TabHovered, 0.26f, 0.59f, 0.98f, 1.00f)
        style.setColor(ImGuiCol.TabActive, 0.06f, 0.53f, 0.98f, 1.00f)
        style.setColor(ImGuiCol.TabUnfocused, 0.26f, 0.59f, 0.98f, 0.40f)
        style.setColor(ImGuiCol.TabUnfocusedActive, 0.26f, 0.59f, 0.98f, 0.40f)
        style.setColor(ImGuiCol.Header, 0.26f, 0.59f, 0.98f, 0.31f)
        style.setColor(ImGuiCol.HeaderHovered, 0.26f, 0.59f, 0.98f, 0.80f)
        style.setColor(ImGuiCol.HeaderActive, 0.26f, 0.59f, 0.98f, 1.00f)
        style.setColor(ImGuiCol.TableHeaderBg, 0.9f, 0.9f, 0.9f, 1.00f)
//    style.setColor(ImGuiCol.Column, 0.39f, 0.39f, 0.39f, 1.00f)
//    style.setColor(ImGuiCol.ColumnHovered, 0.26f, 0.59f, 0.98f, 0.78f)
//    style.setColor(ImGuiCol.ColumnActive, 0.26f, 0.59f, 0.98f, 1.00f)
        style.setColor(ImGuiCol.ResizeGrip, 1.00f, 1.00f, 1.00f, 0.50f)
        style.setColor(ImGuiCol.ResizeGripHovered, 0.26f, 0.59f, 0.98f, 0.67f)
        style.setColor(ImGuiCol.ResizeGripActive, 0.26f, 0.59f, 0.98f, 0.95f)
//    style.setColor(ImGuiCol.CloseButton, 0.59f, 0.59f, 0.59f, 0.50f)
//    style.setColor(ImGuiCol.CloseButtonHovered, 0.98f, 0.39f, 0.36f, 1.00f)
        //style.setColor(ImGuiCol.CloseButtonActive, 0.98f, 0.39f, 0.36f, 1.00f)
        style.setColor(ImGuiCol.PlotLines, 0.39f, 0.39f, 0.39f, 1.00f)
        style.setColor(ImGuiCol.PlotLinesHovered, 1.00f, 0.43f, 0.35f, 1.00f)
        style.setColor(ImGuiCol.PlotHistogram, 0.90f, 0.70f, 0.00f, 1.00f)
        style.setColor(ImGuiCol.PlotHistogramHovered, 1.00f, 0.60f, 0.00f, 1.00f)
        style.setColor(ImGuiCol.TextSelectedBg, 0.26f, 0.59f, 0.98f, 0.35f)
        //style.setColor(ImGuiCol.ModalWindowDarkening, 0.20f, 0.20f, 0.20f, 0.35f)

        if (bStyleDark_) {
            for (i in 0 until ImGuiCol.COUNT) {
                val col = style.colors[i]
                val hsv = FloatArray(4)
                ImGui.colorConvertRGBtoHSV(col, hsv)

                if (hsv[1] < 0.1f) {
                    hsv[2] = 1.0f - hsv[2]
                }
                ImGui.colorConvertHSVtoRGB(hsv, col)
                if (col[3] < 1.00f) {
                    col[3] *= alpha_
                }
                style.setColor(i, col[0], col[1], col[2], col[3])
            }
        } else {
            for (i in 0 until ImGuiCol.COUNT) {
                val col = style.colors[i]
                if (col[3] < 1.00f) {
                    col[0] *= alpha_
                    col[1] *= alpha_
                    col[2] *= alpha_
                    col[3] *= alpha_
                }
                style.setColor(i, col[0], col[1], col[2], col[3])
            }
        }
    }
}