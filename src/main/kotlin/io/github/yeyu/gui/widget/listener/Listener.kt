package io.github.yeyu.gui.widget.listener

interface Listener {

    fun isListenOffFocus(): Boolean

    fun setFocused(focused: Boolean)

    fun isFocused(): Boolean
}