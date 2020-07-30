package io.github.yeyu.gui.widget.children

import com.mojang.blaze3d.systems.RenderSystem
import io.github.yeyu.gui.ScreenRenderer
import io.github.yeyu.gui.ScreenRendererHandler
import io.github.yeyu.gui.inventory.interfaces.ClientInventoryInteractionListener
import io.github.yeyu.gui.inventory.interfaces.InventoryProvider
import io.github.yeyu.gui.widget.ChildWidget
import io.github.yeyu.gui.widget.ParentWidget
import io.github.yeyu.gui.widget.listener.KeyListener
import io.github.yeyu.gui.widget.listener.MouseListener
import io.github.yeyu.util.Classes
import io.github.yeyu.util.DrawerUtil
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.item.ItemStack

class SingleItemSlotWidget(
    override val relativeX: Int = 0,
    override val relativeY: Int = 0,
    private val backgroundColor: Int = DrawerUtil.constructColor(0x80, 0x80, 0x80, 0x55),
    private val activeColor: Int = DrawerUtil.constructColor(0xA0, 0xA0, 0xA0, 0xEE),
    private val slotNumber: Int
) : ChildWidget, MouseListener, KeyListener {

    init {
        check(slotNumber > -1) { "Slot number cannot be empty!" }
    }

    companion object {
        const val SQUARE_SIZE = 18
    }

    var parentWidget: ParentWidget? = null

    // states
    private var hovered = false
    var mouseDown = false

    override fun getParent(): ParentWidget? = parentWidget

    override fun setParent(parent: ParentWidget) {
        parentWidget = parent
    }

    override val width: Int = SQUARE_SIZE
    override val height: Int = SQUARE_SIZE

    override fun render(
        matrices: MatrixStack,
        relativeMouseX: Int,
        relativeMouseY: Int,
        screen: ScreenRenderer<*>
    ) {
        Classes.requireInstanceOf(screen.getHandler(), ClientInventoryInteractionListener::class)
        val handler: ClientInventoryInteractionListener = screen.getHandler() as ClientInventoryInteractionListener
        if (!handler.hasStack(slotNumber)) return
        val absX: Int = getDrawX()
        val absY: Int = getDrawY()

        when {
            handler.hasCalculatedStack(slotNumber) || hovered -> {
                DrawerUtil.coloredRect(absX + 1, absY + 1, width - 2, height - 2, activeColor)
            }
            else -> {
                DrawerUtil.coloredRect(absX + 1, absY + 1, width - 2, height - 2, backgroundColor)
            }
        }

        val stack: ItemStack = handler.getCalculatedStack(slotNumber)
        if (!stack.isEmpty) {
            RenderSystem.enableDepthTest()
            val mc = MinecraftClient.getInstance()
            val renderer = mc.itemRenderer
            renderer.zOffset = 100f
            renderer.renderInGuiWithOverrides(stack, absX + 1, absY + 1)
            renderer.renderGuiItemOverlay(mc.textRenderer, stack, absX + 1, absY + 1)
            renderer.zOffset = 0f
            if (hovered) {
                screen.setItemToRenderTooltip(stack)
            }
        }
    }

    override fun <T : ScreenRendererHandler> onMouseOver(mouseX: Int, mouseY: Int, handler: T) {
        // does nothing
    }

    override fun <T : ScreenRendererHandler> onMouseDown(mouseX: Double, mouseY: Double, button: Int, handler: T) {
        if (!isMouseOver(mouseX, mouseY)) return
        mouseDown = true
        Classes.runUnsafe(handler, ClientInventoryInteractionListener::class, null) {
            it.onSlotClick(slotNumber, button, Screen.hasShiftDown())
        }
    }

    override fun <T : ScreenRendererHandler> onMouseUp(mouseX: Double, mouseY: Double, button: Int, handler: T) {
        if (!mouseDown) return
        mouseDown = false
        Classes.runUnsafe(handler, ClientInventoryInteractionListener::class, null) {
            it.onSlotRelease(slotNumber)
        }
    }

    override fun <T : ScreenRendererHandler> onMouseScroll(mouseX: Double, mouseY: Double, amount: Double, handler: T) {
        // does nothing
    }

    override fun <T : ScreenRendererHandler> onMouseMove(mouseX: Double, mouseY: Double, handler: T) {
        hovered = isMouseOver(mouseX, mouseY)
    }

    override fun <T : ScreenRendererHandler> onKeyPressed(keyCode: Int, scanCode: Int, modifier: Int, handler: T) {
        if (!hovered) return
        if (Classes.getUnsafe(handler, InventoryProvider::class, true) { !it.getCursorStack().isEmpty }) return
        if (!MinecraftClient.getInstance().options.keyDrop.matchesKey(keyCode, scanCode)) return
        Classes.runUnsafe(handler, ClientInventoryInteractionListener::class, null) {
            it.onItemThrow(slotNumber, Screen.hasShiftDown())
        }
    }

    override fun <T : ScreenRendererHandler> onKeyReleased(keyCode: Int, scanCode: Int, modifier: Int, handler: T) {
        // does nothing
    }

    override fun <T : ScreenRendererHandler> onCharTyped(chr: Char, scanCode: Int, handler: T) {
        // does nothing
    }

    override fun isListenOffFocus(): Boolean {
        return false
    }

    override fun setFocused(focused: Boolean) {
        // does nothing
    }

    override fun isFocused(): Boolean {
        return false
    }
}