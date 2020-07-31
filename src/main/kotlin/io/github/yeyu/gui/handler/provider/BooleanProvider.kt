package io.github.yeyu.gui.handler.provider

interface BooleanProvider {
    /**
     * Returns the relevant value based on the name
     * of the widget
     * */
    fun getBoolean(name: String): Boolean
}