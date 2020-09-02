package io.github.yeyu.jamcgui.gui.renderer.widget.listener

import io.github.yeyu.jamcgui.gui.handler.ScreenRendererHandler

/**
 * Keyboard and character input listener
 * */
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