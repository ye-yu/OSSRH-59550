package io.github.yeyu.jamcgui.gui.renderer.widget.parents

import io.github.yeyu.jamcgui.gui.handler.ScreenRendererHandler
import io.github.yeyu.jamcgui.gui.renderer.ScreenRenderer
import io.github.yeyu.jamcgui.gui.renderer.widget.ChildWidget
import io.github.yeyu.jamcgui.gui.renderer.widget.ParentWidget
import io.github.yeyu.jamcgui.gui.renderer.widget.ParentWidget.AnchorType
import io.github.yeyu.jamcgui.util.DrawerUtil
import net.minecraft.client.util.math.MatrixStack

/**
 * A standard parent panel
 * that renders child widgets
 * */
open class Panel(
    override val relativeX: Int = 0,
    override val relativeY: Int = 0,
    override val width: Int = 1,
    override val height: Int = 1,
    override val horizontalAnchor: AnchorType = AnchorType.START,
    override val verticalAnchor: AnchorType = AnchorType.START,
    private val backgroundColor: Int = -0xf3a3b3c,
    override val name: String
) : ParentWidget {

    private val children = ArrayList<ChildWidget>()
    override var parentScreen: ScreenRenderer<out ScreenRendererHandler>? = null

    override fun add(w: ChildWidget) {
        children.add(w)
        w.setParent(this)
    }

    override fun render(
        matrices: MatrixStack,
        relativeMouseX: Int,
        relativeMouseY: Int,
        screen: ScreenRenderer<*>
    ) {
        val drawX: Int = getDrawX()
        val drawY: Int = getDrawY()

        DrawerUtil.coloredRect(drawX, drawY, width, height, backgroundColor)
        for (w in children) {
            w.render(matrices, relativeMouseX, relativeMouseY, screen)
        }
    }

    override fun setFocused(focused: Boolean) {
        // does nothing
    }
}