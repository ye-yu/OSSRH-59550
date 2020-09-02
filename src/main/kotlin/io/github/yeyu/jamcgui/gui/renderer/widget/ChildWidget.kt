package io.github.yeyu.jamcgui.gui.renderer.widget

/**
 * A child widget
 *
 * Note: Add child widget to a parent widget
 * to render widget on the screen.
 * */
interface ChildWidget : Widget {
    fun betweenIncExc(lowerBound: Int, x: Int, upperBound: Int): Boolean {
        return x in lowerBound until upperBound
    }

    fun getParent(): ParentWidget?

    fun setParent(parent: ParentWidget)

    override fun isMouseOver(mouseX: Double, mouseY: Double): Boolean {
        if (getParent() == null) return false
        val absX: Int = getParent()!!.getDrawX() + relativeX
        val absY: Int = getParent()!!.getDrawY() + relativeY
        return (betweenIncExc(absX, mouseX.toInt(), absX + width)
                && betweenIncExc(absY, mouseY.toInt(), absY + height))
    }

    fun getDrawX(): Int {
        return if (getParent() == null) 0 else getParent()!!.getDrawX() + relativeX
    }

    fun getDrawY(): Int {
        return if (getParent() == null) 0 else getParent()!!.getDrawY() + relativeY
    }
}
