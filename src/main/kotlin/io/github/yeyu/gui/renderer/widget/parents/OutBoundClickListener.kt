package io.github.yeyu.gui.renderer.widget.parents

import io.github.yeyu.gui.handler.ScreenRendererHandler
import io.github.yeyu.gui.handler.listener.ClientInventoryInteractionListener
import io.github.yeyu.gui.handler.provider.InventoryProvider
import io.github.yeyu.gui.renderer.ScreenRenderer
import io.github.yeyu.gui.renderer.widget.ChildWidget
import io.github.yeyu.gui.renderer.widget.ParentWidget
import io.github.yeyu.gui.renderer.widget.ParentWidget.AnchorType
import io.github.yeyu.gui.renderer.widget.listener.KeyListener
import io.github.yeyu.gui.renderer.widget.listener.MouseListener
import io.github.yeyu.util.Classes
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.util.math.MatrixStack

/**
 * Useful for sending item throw event outside of the
 * gui.
 * */
class OutBoundClickListener(
    override val relativeX: Int,
    override val relativeY: Int,
    override val width: Int,
    override val height: Int,
    override val horizontalAnchor: AnchorType = AnchorType.START,
    override val verticalAnchor: AnchorType = AnchorType.START,
    override val name: String
) : ParentWidget, MouseListener, KeyListener {

    override var parentScreen: ScreenRenderer<out ScreenRendererHandler>? = null

    /**
     * @throws UnsupportedOperationException Class does not render any child widgets
     * */
    @Deprecated("Class does not render any child widgets", ReplaceWith(""))
    override fun add(w: ChildWidget) {
        throw UnsupportedOperationException("Class does not render any child widgets")
    }

    override fun render(matrices: MatrixStack, relativeMouseX: Int, relativeMouseY: Int, screen: ScreenRenderer<*>) {
        // does nothing
    }

    override fun <T : ScreenRendererHandler> onMouseOver(mouseX: Int, mouseY: Int, handler: T) {
        // does nothing
    }

    override fun <T : ScreenRendererHandler> onMouseDown(mouseX: Double, mouseY: Double, button: Int, handler: T) {
        // does nothing
    }

    override fun <T : ScreenRendererHandler> onMouseUp(mouseX: Double, mouseY: Double, button: Int, handler: T) {
        if (!isMouseOver(mouseX, mouseY)) Classes.runUnsafe(handler, ClientInventoryInteractionListener::class, null) {
            it.onItemThrow(-999, Screen.hasControlDown())
        }
    }

    override fun <T : ScreenRendererHandler> onMouseScroll(mouseX: Double, mouseY: Double, amount: Double, handler: T) {
        // does nothing
    }

    override fun <T : ScreenRendererHandler> onMouseMove(mouseX: Double, mouseY: Double, handler: T) {
        // does nothing
    }

    override fun <T : ScreenRendererHandler> onKeyPressed(keyCode: Int, scanCode: Int, modifier: Int, handler: T) {
        if (Classes.getUnsafe(handler, InventoryProvider::class, true) { it.getCursorStack().isEmpty }) return
        if (!MinecraftClient.getInstance().options.keyDrop.matchesKey(keyCode, scanCode)) return
        Classes.runUnsafe(handler, ClientInventoryInteractionListener::class, null) {
            it.onItemThrow(-999, Screen.hasControlDown())
        }
    }

    override fun <T : ScreenRendererHandler> onKeyReleased(keyCode: Int, scanCode: Int, modifier: Int, handler: T) {
        // does nothing
    }

    override fun <T : ScreenRendererHandler> onCharTyped(chr: Char, scanCode: Int, handler: T) {
        // does nothing
    }

    override fun isListenOffFocus(): Boolean = true

    override fun setFocused(focused: Boolean) {
        // does nothing
    }

    override fun isFocused(): Boolean = true
}