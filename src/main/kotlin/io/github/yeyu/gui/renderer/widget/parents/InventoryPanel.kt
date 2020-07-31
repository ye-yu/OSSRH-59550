package io.github.yeyu.gui.renderer.widget.parents

import io.github.yeyu.gui.handler.ScreenRendererHandler
import io.github.yeyu.gui.renderer.ScreenRenderer
import io.github.yeyu.gui.renderer.widget.ChildWidget
import io.github.yeyu.gui.renderer.widget.ParentWidget
import io.github.yeyu.gui.renderer.widget.ParentWidget.AnchorType
import io.github.yeyu.gui.renderer.widget.children.SingleItemSlotWidget
import io.github.yeyu.gui.renderer.widget.listener.KeyListener
import io.github.yeyu.gui.renderer.widget.listener.MouseListener
import net.minecraft.client.util.math.MatrixStack
import java.util.stream.IntStream

class InventoryPanel(
    override val relativeX: Int = 0,
    override val relativeY: Int = 0,
    override val width: Int = SingleItemSlotWidget.SQUARE_SIZE,
    override val height: Int = SingleItemSlotWidget.SQUARE_SIZE,
    override val horizontalAnchor: AnchorType = AnchorType.START,
    override val verticalAnchor: AnchorType = AnchorType.START,
    private val inventoryIndex: Int = 0,
    private val numberOfSlots: Int = 1,
    private val cols: Int = 1,
    override val name: String
) : ParentWidget, KeyListener, MouseListener {

    override var parentScreen: ScreenRenderer<out ScreenRendererHandler>? = null
    private val children = ArrayList<ChildWidget>()
    private val slots = ArrayList<SingleItemSlotWidget>()

    // states
    private var mouseDown = false
    private var mouseDownButton = -1

    init {
        check(inventoryIndex > -1) { "Inventory index cannot be less than 0." }
        check(numberOfSlots > -1) { "Number of slots cannot be less than 0." }
        check(cols > 0) { "Number of columns cannot be less than 1." }
        createItemSlots()
    }

    private fun createItemSlots() {
        val squareSize = SingleItemSlotWidget.SQUARE_SIZE
        IntStream.range(inventoryIndex, inventoryIndex + numberOfSlots).forEach {
            val x = Math.floorMod(it - inventoryIndex, cols) * squareSize // always start from 0
            val y = Math.floorDiv(it - inventoryIndex, cols) * squareSize
            val itemSlot = SingleItemSlotWidget(
                relativeX = x,
                relativeY = y,
                slotNumber = it,
                name = "slot-$it"
            )
            itemSlot.parentWidget = this
            slots.add(itemSlot)
            children.add(itemSlot)
        }
    }

    override fun add(w: ChildWidget) {
        check(w !is SingleItemSlotWidget) { "Slot widgets are added automatically through inventory index reference!" }
        children.add(w)
    }

    override fun isFocused(): Boolean = false // not focusable
    override fun isListenOffFocus(): Boolean = true // always listen off focus
    override fun setFocused(focused: Boolean) {
        // does nothing
    }

    override fun render(matrices: MatrixStack, relativeMouseX: Int, relativeMouseY: Int, screen: ScreenRenderer<*>) {
        children.forEach { it.render(matrices, relativeMouseX, relativeMouseY, screen) }
    }

    override fun <T : ScreenRendererHandler> onCharTyped(chr: Char, scanCode: Int, handler: T) {
        slots.forEach { it.onCharTyped(chr, scanCode, handler) }
    }

    override fun <T : ScreenRendererHandler> onKeyPressed(keyCode: Int, scanCode: Int, modifier: Int, handler: T) {
        slots.forEach { it.onKeyPressed(keyCode, scanCode, modifier, handler) }
    }

    override fun <T : ScreenRendererHandler> onKeyReleased(keyCode: Int, scanCode: Int, modifier: Int, handler: T) {
        slots.forEach { it.onKeyReleased(keyCode, scanCode, modifier, handler) }
    }

    override fun <T : ScreenRendererHandler> onMouseDown(mouseX: Double, mouseY: Double, button: Int, handler: T) {
        mouseDown = true
        mouseDownButton = button
        slots.forEach { it.onMouseDown(mouseX, mouseY, button, handler) }
    }

    override fun <T : ScreenRendererHandler> onMouseUp(mouseX: Double, mouseY: Double, button: Int, handler: T) {
        mouseDown = false
        mouseDownButton = -1
        slots.forEach { it.onMouseUp(mouseX, mouseY, button, handler) }
    }

    override fun <T : ScreenRendererHandler> onMouseMove(mouseX: Double, mouseY: Double, handler: T) {
        slots.forEach { e ->
            e.onMouseMove(
                mouseX,
                mouseY,
                handler
            )
        }


        if (!mouseDown) return
        slots.stream() // only call those that has no mouse down yet
            .filter { singleItemSlot ->
                singleItemSlot.isMouseOver(
                    mouseX,
                    mouseY
                ) && !singleItemSlot.mouseDown
            }
            .findFirst().ifPresent { e ->
                e.onMouseDown(
                    mouseX,
                    mouseY,
                    mouseDownButton,
                    handler
                )
            }
    }

    override fun <T : ScreenRendererHandler> onMouseOver(mouseX: Int, mouseY: Int, handler: T) {
        // does nothing
    }

    override fun <T : ScreenRendererHandler> onMouseScroll(mouseX: Double, mouseY: Double, amount: Double, handler: T) {
        // does nothing
    }
}