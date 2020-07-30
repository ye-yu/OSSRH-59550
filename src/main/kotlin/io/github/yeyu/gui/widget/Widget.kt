package io.github.yeyu.gui.widget

import io.github.yeyu.gui.ScreenRenderer
import net.minecraft.client.util.math.MatrixStack

interface Widget {
    val relativeX: Int
    val relativeY: Int
    val width: Int
    val height: Int

    fun isMouseOver(mouseX: Double, mouseY: Double): Boolean {
        return mouseX >= relativeX && mouseX < relativeX + width && mouseY >= relativeY && mouseY < relativeY + height
    }

    fun render(
        matrices: MatrixStack,
        relativeMouseX: Int,
        relativeMouseY: Int,
        screen: ScreenRenderer<*>
    )

    fun setFocused(focused:Boolean)
}
