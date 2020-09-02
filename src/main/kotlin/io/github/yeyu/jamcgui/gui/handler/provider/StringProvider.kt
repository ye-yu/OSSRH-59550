package io.github.yeyu.jamcgui.gui.handler.provider

interface StringProvider {
    /**
     * Returns the relevant value based on the name
     * of the widget
     * */
    fun getString(name: String): String
}