package io.github.yeyu.gui.widget.children

import io.github.yeyu.gui.ScreenRenderer
import io.github.yeyu.gui.widget.ChildWidget
import io.github.yeyu.gui.widget.ParentWidget
import io.github.yeyu.util.DrawerUtil
import net.minecraft.client.MinecraftClient
import net.minecraft.client.util.math.MatrixStack

class LabelWidget(
    override val relativeX: Int = 0,
    override val relativeY: Int = 0,
    private val color: Int = DrawerUtil.constructColor(0xFF, 0xFF, 0xFF, 0xFF),
    private val label: String
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

    override fun render(
        matrices: MatrixStack,
        relativeMouseX: Int,
        relativeMouseY: Int,
        screen: ScreenRenderer<*>
    ) {
        val drawX = getDrawX()
        val drawY = getDrawY()
        renderer.draw(
            matrices,
            label,
            drawX.toFloat(),
            drawY.toFloat(),
            color
        )
    }

    override fun setFocused(focused: Boolean) {
        // does nothing
    }
}