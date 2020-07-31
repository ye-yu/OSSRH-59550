package io.github.yeyu.gui.handler.provider

interface EnumProvider {
    /**
     * Returns the relevant value based on the name
     * of the widget
     * */
    fun <T: Enum<T>> getEnum(name: String): T
}