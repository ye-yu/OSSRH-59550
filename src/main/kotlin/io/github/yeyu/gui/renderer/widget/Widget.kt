package io.github.yeyu.gui.renderer.widget

import io.github.yeyu.gui.renderer.ScreenRenderer
import net.minecraft.client.util.math.MatrixStack

/**
 * A common interface for child widgets and
 * parent widgets.
 * */
interface Widget {
    /**
     * The x coord relative to the parent widget
     * */
    val relativeX: Int
    /**
     * The y coord relative to the parent widget
     * */
    val relativeY: Int
    val width: Int
    val height: Int

    /**
     * The name of the widget
     *
     * Note: name uniqueness is not checked
     * */
    val name: String

    fun isMouseOver(mouseX: Double, mouseY: Double): Boolean {
        return mouseX >= relativeX && mouseX < relativeX + width && mouseY >= relativeY && mouseY < relativeY + height
    }

    fun render(
        matrices: MatrixStack,
        relativeMouseX: Int,
        relativeMouseY: Int,
        screen: ScreenRenderer<*>
    )

    fun setFocused(focused: Boolean)
}
