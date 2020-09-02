package io.github.yeyu.jamcgui.gui.handler.listener

interface DoubleListener {
    /**
     * Sets the double value of the given widget name
     * */
    fun onDoubleChanged(db: Double, name: String)
}