package io.github.yeyu.gui.widget.listener

import io.github.yeyu.gui.ScreenRendererHandler

interface KeyListener : Listener {
    fun <T : ScreenRendererHandler> onKeyPressed(
        keyCode: Int,
        scanCode: Int,
        modifier: Int,
        handler: T
    )

    fun <T : ScreenRendererHandler> onKeyReleased(
        keyCode: Int,
        scanCode: Int,
        modifier: Int,
        handler: T
    )

    fun <T : ScreenRendererHandler> onCharTyped(chr: Char, scanCode: Int, handler: T)

}