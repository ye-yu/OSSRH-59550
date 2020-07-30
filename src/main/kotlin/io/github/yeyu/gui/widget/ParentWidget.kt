package io.github.yeyu.gui.widget

import io.github.yeyu.gui.ScreenRenderer
import io.github.yeyu.gui.ScreenRendererHandler

/**
 * For parent widgets, [relativeX] and [relativeY] are effectively
 * the screen coordinates
 * */
interface ParentWidget : Widget {
    val horizontalAnchor: AnchorType
    val verticalAnchor: AnchorType
    var parentScreen: ScreenRenderer<out ScreenRendererHandler>?

    fun add(w: ChildWidget)

    fun getDrawX(): Int {
        var absX = relativeX
        if (horizontalAnchor === AnchorType.END) absX += parentScreen!!.width - width
        else if (horizontalAnchor === AnchorType.MIDDLE)
            absX += (parentScreen!!.width - width) / 2
        return absX
    }

    fun getDrawY(): Int {
        var absY = relativeY
        if (verticalAnchor === AnchorType.END) absY += parentScreen!!.height - height
        else if (verticalAnchor === AnchorType.MIDDLE)
            absY += (parentScreen!!.height - height) / 2
        return absY
    }

    enum class AnchorType {
        START, MIDDLE, END
    }
}