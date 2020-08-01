package io.github.yeyu.gui.renderer.widget.children

import io.github.yeyu.gui.renderer.ScreenRenderer
import io.github.yeyu.gui.renderer.widget.ChildWidget
import io.github.yeyu.gui.renderer.widget.ParentWidget
import io.github.yeyu.gui.renderer.widget.ParentWidget.AnchorType
import io.github.yeyu.util.DrawerUtil
import net.minecraft.client.MinecraftClient
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.text.Text

/**
 * A static label widget
 * */
class LabelWidget(
    override val relativeX: Int = 0,
    override val relativeY: Int = 0,
    private val color: Int = DrawerUtil.constructColor(0xFF, 0xFF, 0xFF, 0xFF),
    private val label: Text,
    override val name: String,
    horizontalAnchor: AnchorType = AnchorType.START,
    verticalAnchor: AnchorType = AnchorType.START,
    private val shadow: Boolean
) : ChildWidget {

    private var parent: ParentWidget? = null

    override fun getParent(): ParentWidget? {
        return parent
    }

    override fun setParent(parent: ParentWidget) {
        this.parent = parent
    }

    private val renderer = MinecraftClient.getInstance().textRenderer
    override val width: Int
        get() = renderer.getWidth(label)
    override val height: Int = renderer.fontHeight
    private val offsetX = when(horizontalAnchor) {
        AnchorType.START -> 0
        AnchorType.MIDDLE -> -(width / 2.0).toInt()
        AnchorType.END -> -width
    }
    private val offsetY = when(verticalAnchor) {
        AnchorType.START -> 0
        AnchorType.MIDDLE -> -(height / 2.0).toInt()
        AnchorType.END -> -height
    }

    override fun render(
        matrices: MatrixStack,
        relativeMouseX: Int,
        relativeMouseY: Int,
        screen: ScreenRenderer<*>
    ) {
        val drawX = getDrawX()
        val drawY = getDrawY()
        if (shadow) {
            renderer.drawWithShadow(
                matrices,
                label,
                drawX.toFloat() + offsetX,
                drawY.toFloat() + offsetY,
                color
            )
        } else {
            renderer.draw(
                matrices,
                label,
                drawX.toFloat() + offsetX,
                drawY.toFloat() + offsetY,
                color
            )
        }
    }

    override fun setFocused(focused: Boolean) {
        // does nothing
    }
}