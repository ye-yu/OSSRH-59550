package io.github.yeyu.gui

import net.minecraft.screen.ScreenHandler
import net.minecraft.screen.ScreenHandlerType

abstract class ScreenRendererHandler(type: ScreenHandlerType<*>?, syncId: Int) : ScreenHandler(type, syncId)
