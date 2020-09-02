package io.github.yeyu.jamcgui.gui.handler.provider

interface IntegerProvider {
    /**
     * Returns the relevant value based on the name
     * of the widget
     * */
    fun getInteger(name: String): Int
}