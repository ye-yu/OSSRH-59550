package io.github.yeyu.jamcgui.gui.handler.listener

interface StringListener {
    /**
     * Sets the string value of the given widget name
     * */
    fun onStringChange(str: String, name: String)
}