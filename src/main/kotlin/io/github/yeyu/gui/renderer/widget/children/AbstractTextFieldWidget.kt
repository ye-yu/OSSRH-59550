package io.github.yeyu.gui.renderer.widget.children

import io.github.yeyu.gui.handler.ScreenRendererHandler
import io.github.yeyu.gui.handler.listener.StringListener
import io.github.yeyu.gui.handler.provider.StringProvider
import io.github.yeyu.gui.renderer.ScreenRenderer
import io.github.yeyu.gui.renderer.widget.ChildWidget
import io.github.yeyu.gui.renderer.widget.ParentWidget
import io.github.yeyu.gui.renderer.widget.listener.KeyListener
import io.github.yeyu.gui.renderer.widget.listener.MouseListener
import io.github.yeyu.util.Classes
import io.github.yeyu.util.DrawerUtil
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.util.math.MatrixStack
import org.apache.commons.lang3.StringUtils
import org.lwjgl.glfw.GLFW

/**
 * A text field widget
 * with text field functionality
 * */
abstract class AbstractTextFieldWidget(
    override val relativeX: Int = 0,
    override val relativeY: Int = 0,
    override val width: Int = 1,
    override val height: Int = 1,
    private val textColor: Int = DrawerUtil.constructColor(0xFF, 0xFF, 0xFF, 0xFF),
    private val caretColor: Int = DrawerUtil.constructColor(0xFF, 0xFF, 0xFF, 0xFF),
    override val name: String
) : ChildWidget, KeyListener, MouseListener {

    private var focused = false

    private var cursor = 0
    private var visibleLower = 0
    private var visibleUpper = 0
    private var hasSelection = false
    private var selection = 0

    private val caretMaxTick = 40
    private var caretTick = caretMaxTick
    private var drawCaret = false

    private var text = ""
    private val textRenderer = MinecraftClient.getInstance().textRenderer
    private val keyboard = MinecraftClient.getInstance().keyboard

    private var parent: ParentWidget? = null

    final override fun getParent(): ParentWidget? {
        return parent
    }

    final override fun setParent(parent: ParentWidget) {
        this.parent = parent
    }

    final override fun render(
        matrices: MatrixStack,
        relativeMouseX: Int,
        relativeMouseY: Int,
        screen: ScreenRenderer<*>
    ) {
        validateCursor()
        updateText(screen.getHandler())
        val absX = getDrawX()
        val absY = getDrawY()

        drawBackground(screen, matrices, absX, absY)

        if (text.isNotEmpty()) textRenderer.draw(
            matrices,
            text.substring(visibleLower, visibleUpper),
            absX.toFloat(),
            absY.toFloat(),
            textColor
        )

        if (!focused) return
        if (--caretTick < 0) {
            caretTick = caretMaxTick
            drawCaret = !drawCaret
        }

        val caretX: Int = textRenderer.getWidth(text.substring(visibleLower, cursor))
        if (drawCaret) {
            DrawerUtil.coloredRect(
                caretX + absX,
                absY - 1,
                1,
                textRenderer.fontHeight + 1,
                caretColor
            )
        }

        if (hasSelection) {
            val drawX: Int
            val drawWidth: Int
            if (selection < cursor) {
                val drawIndex = selection.coerceAtLeast(visibleLower)
                drawX = textRenderer.getWidth(text.substring(visibleLower, drawIndex))
                drawWidth = textRenderer.getWidth(text.substring(drawIndex, cursor))
            } else {
                val drawIndex = selection.coerceAtMost(visibleUpper)
                drawX = textRenderer.getWidth(text.substring(visibleLower, cursor))
                drawWidth = textRenderer.getWidth(text.substring(cursor, drawIndex))
            }
            DrawerUtil.invertedRect(absX + drawX, absY - 1, drawWidth, textRenderer.fontHeight + 1)
        }
    }

    private fun updateText(handler: ScreenRendererHandler) {
        text = Classes.getUnsafe(handler, StringProvider::class, text) { it.getString(name) }
    }

    /**
     * Note: Extend to draw text field background
     * */
    abstract fun drawBackground(
        screen: ScreenRenderer<*>,
        matrices: MatrixStack,
        absX: Int,
        absY: Int
    )

    private fun validateCursor() {
        cursor = cursor.coerceAtMost(text.length)
        cursor = cursor.coerceAtLeast(0)

        if (visibleLower == visibleUpper || visibleLower < 0 || visibleUpper > text.length) {
            // try to get new visible index
            visibleUpper = cursor
            visibleLower = (cursor - textRenderer.trimToWidth(
                text,
                width,
                true
            ).length).coerceAtLeast(0)
        }

        visibleUpper = visibleLower + textRenderer.trimToWidth(text.substring(visibleLower), width).length

        if (cursor < visibleLower) {
            visibleLower = cursor
            visibleUpper = visibleLower + textRenderer.trimToWidth(text.substring(cursor), width).length
        }

        if (cursor > visibleUpper) {
            visibleUpper = cursor
            visibleLower = cursor - textRenderer.trimToWidth(text, width, true).length
        }
    }

    final override fun setFocused(focused: Boolean) {
        this.focused = focused
    }

    final override fun isFocused(): Boolean = focused
    final override fun isListenOffFocus(): Boolean = false

    final override fun <T : ScreenRendererHandler> onCharTyped(chr: Char, scanCode: Int, handler: T) {
        if (!focused) return
        write(chr)
        Classes.runUnsafe(handler, StringListener::class, null) { it.onStringChange(text, name) }
    }

    final override fun <T : ScreenRendererHandler> onKeyPressed(
        keyCode: Int,
        scanCode: Int,
        modifier: Int,
        handler: T
    ) {
        if (!focused) return
        if (Screen.hasShiftDown() && !hasSelection) {
            pinSelection()
        } // otherwise cancel selection on cursor move / key input

        when {
            Screen.isSelectAll(keyCode) -> {
                selectAll()
            }
            Screen.isCopy(keyCode) -> {
                keyboard.clipboard = getSelectedText()
            }
            Screen.isPaste(keyCode) -> {
                this.write(keyboard.clipboard)
            }
            Screen.isCut(keyCode) -> {
                keyboard.clipboard = getSelectedText()
                erase(false)
            }
            else -> {
                when (keyCode) {
                    GLFW.GLFW_KEY_BACKSPACE -> {
                        erase(false)
                    }
                    GLFW.GLFW_KEY_DELETE -> {
                        erase(true)
                    }
                    GLFW.GLFW_KEY_RIGHT -> {
                        moveCursor(true, Screen.hasControlDown())
                        if (!Screen.hasShiftDown()) releaseSelection()
                    }
                    GLFW.GLFW_KEY_LEFT -> {
                        this.moveCursor(false, Screen.hasControlDown())
                        if (!Screen.hasShiftDown()) releaseSelection()
                    }
                    GLFW.GLFW_KEY_HOME -> {
                        this.setCursorToStart()
                        if (!Screen.hasShiftDown()) releaseSelection()
                    }
                    GLFW.GLFW_KEY_END -> {
                        this.setCursorToEnd()
                        if (!Screen.hasShiftDown()) releaseSelection()
                    }
                }
            }
        }
        Classes.runUnsafe(handler, StringListener::class, null) { it.onStringChange(text, name) }
    }

    final override fun <T : ScreenRendererHandler> onKeyReleased(
        keyCode: Int,
        scanCode: Int,
        modifier: Int,
        handler: T
    ) {
        // does nothing
    }

    final override fun <T : ScreenRendererHandler> onMouseDown(
        mouseX: Double,
        mouseY: Double,
        button: Int,
        handler: T
    ) {
        focused = isMouseOver(mouseX, mouseY)
    }

    final override fun <T : ScreenRendererHandler> onMouseUp(mouseX: Double, mouseY: Double, button: Int, handler: T) {
        // does nothing
    }

    final override fun <T : ScreenRendererHandler> onMouseMove(mouseX: Double, mouseY: Double, handler: T) {
        // does nothing
    }

    final override fun <T : ScreenRendererHandler> onMouseOver(mouseX: Int, mouseY: Int, handler: T) {
        // odes nothing
    }

    final override fun <T : ScreenRendererHandler> onMouseScroll(
        mouseX: Double,
        mouseY: Double,
        amount: Double,
        handler: T
    ) {
        if (focused && isMouseOver(mouseX, mouseY) && amount != 0.0) {
            moveCursor(amount < 0, Screen.hasControlDown())
        }
    }

    private fun selectAll() {
        cursor = 0
        pinSelection()
        cursor = text.length

    }

    private fun moveCursor(forward: Boolean, skipWord: Boolean) {
        if (forward) {
            if (skipWord) {
                val newCursor = text.indexOf(' ', cursor)
                if (newCursor == -1) {
                    setCursorToEnd()
                } else {
                    cursor = (newCursor + 1).coerceAtMost(text.length)
                }
            } else {
                cursor = (cursor + 1).coerceAtMost(text.length)
            }
        } else {
            if (skipWord) {
                val newCursor =
                    StringUtils.reverse(text.substring(0, cursor)).indexOf(' ')
                if (newCursor == -1) {
                    setCursorToStart()
                } else {
                    cursor = cursor - newCursor - 1
                }
            } else {
                cursor = (cursor - 1).coerceAtLeast(0)
            }
        }
    }

    private fun setCursorToStart() {
        cursor = 0
    }

    private fun setCursorToEnd() {
        cursor = text.length
    }

    private fun write(word: String) {
        for (c in word.toCharArray()) {
            write(c)
        }
    }

    open fun write(c: Char) {
        val lower: Int
        val upper: Int
        if (hasSelection) {
            lower = cursor.coerceAtMost(selection)
            upper = cursor.coerceAtLeast(selection)
        } else {
            upper = cursor
            lower = upper
        }

        text = text.substring(0, lower) + c + text.substring(upper)
        cursor = (cursor + 1).coerceAtMost(text.length)
        releaseSelection()
    }

    private fun erase(forward: Boolean) {
        when {
            hasSelection -> {
                val lower = cursor.coerceAtMost(selection)
                val upper = cursor.coerceAtLeast(selection)
                text = text.substring(0, lower) + text.substring(upper)
                releaseSelection()
            }
            forward -> {
                if (cursor >= text.length) return  // cant delete if it reached the end
                text = text.substring(0, cursor) + text.substring(cursor + 1)
            }
            else -> {
                if (cursor <= 0) return  // cant delete if it reached the start
                text = text.substring(0, cursor - 1) + text.substring(cursor)
                cursor = (cursor - 1).coerceAtLeast(0)
            }
        }
    }

    private fun pinSelection() {
        selection = cursor
        hasSelection = true
    }

    private fun releaseSelection() {
        hasSelection = false
    }

    private fun getSelectedText(): String? {
        if (!hasSelection) return ""
        check(
            selection != cursor
        ) { "Error in programming! Is selecting but cursor is the same as selection" }
        val lower = selection.coerceAtMost(cursor)
        val upper = selection.coerceAtLeast(cursor)
        return text.substring(lower, upper)
    }
}