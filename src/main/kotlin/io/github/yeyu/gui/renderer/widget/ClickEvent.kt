package io.github.yeyu.gui.renderer.widget

import org.lwjgl.glfw.GLFW

/**
 * A click event data
 * */
data class ClickEvent(val button: Int, val hasShiftDown: Boolean) {

    override fun hashCode(): Int {
        var result = button
        result = 31 * result + hasShiftDown.hashCode()
        return result
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ClickEvent

        if (button != other.button) return false
        if (hasShiftDown != other.hasShiftDown) return false

        return true
    }

    companion object {
        val LEFT =
            ClickEvent(GLFW.GLFW_MOUSE_BUTTON_LEFT, false)
        val MIDDLE =
            ClickEvent(GLFW.GLFW_MOUSE_BUTTON_MIDDLE, false)
        val RIGHT =
            ClickEvent(GLFW.GLFW_MOUSE_BUTTON_RIGHT, false)
        val SHIFT_LEFT =
            ClickEvent(GLFW.GLFW_MOUSE_BUTTON_LEFT, true)
        val SHIFT_MIDDLE =
            ClickEvent(GLFW.GLFW_MOUSE_BUTTON_MIDDLE, true)
        val SHIFT_RIGHT =
            ClickEvent(GLFW.GLFW_MOUSE_BUTTON_RIGHT, true)

        fun of(button: Int, hasShiftDown: Boolean): ClickEvent {
            return when (button) {
                GLFW.GLFW_MOUSE_BUTTON_LEFT -> if (hasShiftDown) SHIFT_LEFT else LEFT
                GLFW.GLFW_MOUSE_BUTTON_MIDDLE -> if (hasShiftDown) SHIFT_MIDDLE else MIDDLE
                GLFW.GLFW_MOUSE_BUTTON_RIGHT -> if (hasShiftDown) SHIFT_RIGHT else RIGHT
                else -> ClickEvent(button, hasShiftDown)
            }
        }
    }
}
