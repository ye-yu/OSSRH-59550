package io.github.yeyu.gui.handler.listener

interface DoubleListener {
    /**
     * Sets the double value of the given widget name
     * */
    fun onDoubleChanged(db: Double, name: String)
}