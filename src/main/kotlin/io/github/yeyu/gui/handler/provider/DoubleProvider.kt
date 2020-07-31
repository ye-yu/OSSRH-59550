package io.github.yeyu.gui.handler.provider

interface DoubleProvider {
    /**
     * Returns the relevant value based on the name
     * of the widget
     * */
    fun getDouble(name: String): Double
}