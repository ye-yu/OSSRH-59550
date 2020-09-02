package io.github.yeyu.jamcgui.gui.handler

import net.minecraft.screen.ScreenHandler
import net.minecraft.screen.ScreenHandlerType

/**
 * A base screen handler for screen renderer
 * @see io.github.yeyu.jamcgui.gui.renderer.ScreenRenderer
 * */
abstract class ScreenRendererHandler(type: ScreenHandlerType<*>?, syncId: Int) : ScreenHandler(type, syncId)
