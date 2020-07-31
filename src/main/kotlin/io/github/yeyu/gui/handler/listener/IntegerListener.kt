package io.github.yeyu.gui.handler.listener

interface IntegerListener {
    /**
     * Sets the integer value of the given widget name
     * */
    fun onIntegerChanged(n: Int, name: String)
}