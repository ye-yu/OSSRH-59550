package io.github.yeyu.jamcgui.gui.renderer.widget.listener

/**
 * Base interface for listeners.
 * Does nothing when implemented.
 * Use [KeyListener] or [MouseListener]
 * instead.
 * */
interface Listener {

    fun isListenOffFocus(): Boolean

    fun setFocused(focused: Boolean)

    fun isFocused(): Boolean
}