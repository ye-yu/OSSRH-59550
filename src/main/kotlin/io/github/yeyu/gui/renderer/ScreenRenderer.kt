package io.github.yeyu.gui.renderer

import com.mojang.blaze3d.systems.RenderSystem
import io.github.yeyu.gui.handler.ClientScreenHandler
import io.github.yeyu.gui.handler.ScreenRendererHandler
import io.github.yeyu.gui.renderer.widget.ParentWidget
import io.github.yeyu.gui.renderer.widget.Widget
import io.github.yeyu.gui.renderer.widget.listener.KeyListener
import io.github.yeyu.gui.renderer.widget.listener.Listener
import io.github.yeyu.gui.renderer.widget.listener.MouseListener
import io.github.yeyu.util.Classes
import io.github.yeyu.util.Scissors
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.Element
import net.minecraft.client.gui.screen.ingame.HandledScreen
import net.minecraft.client.render.DiffuseLighting
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.item.ItemStack
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import org.lwjgl.glfw.GLFW
import org.lwjgl.opengl.GL11
import java.util.*
import kotlin.collections.ArrayList

/**
 * A modified handled screen to cater widget & listener features
 * */
open class ScreenRenderer<T : ScreenRendererHandler>(
    handler: T,
    inventory: PlayerInventory,
    title: Text,
    private val texture: Identifier
) :
    HandledScreen<T>(handler, inventory, title) {

    var toRenderTooltip: ItemStack = ItemStack.EMPTY
    protected val parents = ArrayList<ParentWidget>()
    protected val listeners = ArrayList<Listener>()

    init {
        children.clear()
        this.init()
        Classes.requireInstanceOf(handler, ClientScreenHandler::class)
        (handler as ClientScreenHandler).sendInitPacket()
    }

    override fun init(client: MinecraftClient, width: Int, height: Int) {
        this.client = client
        itemRenderer = client.itemRenderer
        textRenderer = client.textRenderer
        this.width = width
        this.height = height
        buttons.clear()
        children.clear()
        super.init()
    }

    open fun renderTooltip(matrices: MatrixStack?, x: Int, y: Int) {
        if (playerInventory.cursorStack.isEmpty && toRenderTooltip != ItemStack.EMPTY) {
            super.renderTooltip(matrices, toRenderTooltip, x, y)
            toRenderTooltip = ItemStack.EMPTY
        }
    }

    fun setItemToRenderTooltip(item: ItemStack) {
        toRenderTooltip = item
    }

    /**
     * @throws UnsupportedOperationException Use [addParent] or [addListener] instead!
     */
    @Deprecated(
        "Use addParent or addListener instead!",
        ReplaceWith(
            "addParent(child) or addListener(child)",
            "io.github.yeyu.gui.renderer.widget.ParentWidget",
            "io.github.yeyu.gui.renderer.widget.listener.Listener"
        )
    )
    override fun <E : Element?> addChild(child: E): E {
        throw UnsupportedOperationException("Use addParent or addListener instead!")
    }

    /**
     * Add a parent widget to the renderer. Parent widgets are responsible
     * for rendering their own children relative to their own coordinate.
     */
    protected open fun <E : ParentWidget> addParent(parent: E) {
        this.parents.add(parent)
        parent.parentScreen = this
    }

    /**
     * Add widget that implements listener and any of its
     * subclass
     *
     * @see io.github.yeyu.gui.renderer.widget.listener.MouseListener
     * @see io.github.yeyu.gui.renderer.widget.listener.KeyListener
     */
    protected open fun <E : Listener> addListener(listener: E) {
        this.listeners.add(listener)
    }

    /**
     * Used when cycling through elements
     */
    override fun changeFocus(lookForwards: Boolean): Boolean {
        return false
    }

    open fun getHandler(): T {
        return handler
    }

    override fun render(matrices: MatrixStack?, mouseX: Int, mouseY: Int, delta: Float) {
        hoveredWidget(
            mouseX.toDouble(),
            mouseY.toDouble()
        ).ifPresent { hoveredElement ->
            Classes.runUnsafe(hoveredElement, MouseListener::class, "") {
                it.onMouseOver(mouseX, mouseY, handler)
            }
        }
        drawBackground(matrices, delta, mouseX, mouseY)
        GL11.glEnable(GL11.GL_SCISSOR_TEST)
        Scissors.refreshScissors()
        for (e in parents) {
            e.render(matrices!!, mouseX - x, mouseY - y, this)
        }
        GL11.glDisable(GL11.GL_SCISSOR_TEST)
        Scissors.checkStackIsEmpty()
        DiffuseLighting.disable() //Needed because super.render leaves dirty state
        // draw cursor item stack

        val cursorStack = playerInventory.cursorStack
        if (!cursorStack.isEmpty) {
            @Suppress("DEPRECATION")
            RenderSystem.translatef(0.0f, 0.0f, 32.0f)
            zOffset = 200
            itemRenderer.zOffset = 200.0f
            itemRenderer.renderInGuiWithOverrides(cursorStack, mouseX - 9, mouseY - 9)
            itemRenderer.renderGuiItemOverlay(
                textRenderer,
                cursorStack,
                mouseX - 9,
                mouseY - 9,
                if (cursorStack.count > 1) String.format("%d", cursorStack.count) else null
            )
            zOffset = 0
            itemRenderer.zOffset = 0.0f
        }
        renderTooltip(matrices, mouseX, mouseY)
    }

    private fun hoveredWidget(
        mouseX: Double,
        mouseY: Double
    ): Optional<Widget> {
        return try {
            listeners
                .stream()
                .filter { e: Listener -> e is Widget }
                .map { e: Listener -> e as Widget }
                .filter { e: Widget -> e.isMouseOver(mouseX, mouseY) }
                .findAny()
        } catch (npe: NullPointerException) {
            Optional.empty()
        }
    }

    override fun hoveredElement(mouseX: Double, mouseY: Double): Optional<Element> {
        return Optional.empty()
    }

    override fun mouseMoved(mouseX: Double, mouseY: Double) {
        listeners.forEach { e ->
            Classes.runUnsafe(e, MouseListener::class, null) {
                it.onMouseMove(mouseX, mouseY, handler)
            }
        }
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        listeners.forEach { e ->
            Classes.runUnsafe(e, MouseListener::class, null) {
                it.onMouseDown(mouseX, mouseY, button, handler)
            }
        }
        return true
    }

    override fun mouseReleased(mouseX: Double, mouseY: Double, button: Int): Boolean {
        listeners.forEach { e: Listener ->
            Classes.runUnsafe(e, MouseListener::class, null) {
                it.onMouseUp(mouseX, mouseY, button, handler)
            }
        }
        return true
    }

    override fun mouseScrolled(mouseX: Double, mouseY: Double, amount: Double): Boolean {
        listeners.forEach { e ->
            Classes.runUnsafe(e, MouseListener::class, null) {
                it.onMouseScroll(mouseX, mouseY, amount, handler)
            }
        }
        return true
    }

    override fun shouldCloseOnEsc(): Boolean {
        return true
    }

    /**
     * @return true if at least one of the listener accepts input
     */
    override fun keyReleased(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
        return listeners.stream().mapToInt {
            Classes.getUnsafe(it, KeyListener::class, 0) { element ->
                if (element.isListenOffFocus() || element.isFocused()) {
                    element.onKeyReleased(keyCode, scanCode, modifiers, handler)
                    return@getUnsafe 1
                } else {
                    return@getUnsafe 0
                }
            }
        }.sum() > 0
    }

    /**
     * @return true if at least one of the listener accepts input
     */
    override fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
        if (shouldCloseOnEsc() && keyCode == GLFW.GLFW_KEY_ESCAPE) {
            onClose()
            try {
                client!!.player!!.closeHandledScreen()
            } catch (ignored: NullPointerException) {
            }
            return true
        }
        return listeners.stream().mapToInt {
            Classes.getUnsafe(it, KeyListener::class, 0) { element ->
                if (element.isListenOffFocus() || element.isFocused()) {
                    element.onKeyPressed(keyCode, scanCode, modifiers, handler)
                    return@getUnsafe 1
                } else {
                    return@getUnsafe 0
                }
            }
        }.sum() > 0
    }

    /**
     * @return true if at least one of the listener accepts input
     */
    override fun charTyped(chr: Char, keyCode: Int): Boolean {
        return listeners.stream().mapToInt {
            Classes.getUnsafe(it, KeyListener::class, 0) { element ->
                if (element.isListenOffFocus() || element.isFocused()) {
                    element.onCharTyped(chr, keyCode, handler)
                    return@getUnsafe 1
                } else {
                    return@getUnsafe 0
                }
            }
        }.sum() > 0
    }

    /**
     *
     * */
    @Deprecated(
        "Delegate focus events on the widget itself!",
        ReplaceWith("setFocused", "io.github.yeyu.gui.renderer.widget.Widget")
    )
    override fun setInitialFocus(element: Element?) {
        throw UnsupportedOperationException("Delegate focus events on the widget itself!")
    }

    @Deprecated(
        "Delegate focus events on the widget itself!",
        ReplaceWith("onMouseDown", "io.github.yeyu.gui.renderer.widget.listener.Listener")
    )
    override fun setFocused(element: Element?) {
        throw UnsupportedOperationException("Delegate focus events on the widget itself!")
    }

    @Deprecated(
        "Delegate focus events on the widget itself!",
        ReplaceWith("render", "io.github.yeyu.gui.renderer.widget.ParentWidget")
    )

    override fun drawForeground(matrices: MatrixStack?, mouseX: Int, mouseY: Int) {
        throw UnsupportedOperationException("Delegate title rendering on the widget itself!")
    }

    override fun onClose() {
        listeners.clear()
        parents.clear()
        super.onClose()
    }

    override fun drawBackground(matrices: MatrixStack?, delta: Float, mouseX: Int, mouseY: Int) {
        if (this.client == null) return

        @Suppress("DEPRECATION")
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f)
        client!!.textureManager.bindTexture(this.texture)
        val middleX = (width - backgroundWidth) / 2
        val middleY = (height - backgroundHeight) / 2
        this.drawTexture(matrices, middleX, middleY, 0, 0, backgroundWidth, backgroundHeight)
    }
}