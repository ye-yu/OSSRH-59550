package io.github.yeyu.jamcgui.gui.handler.listener

interface BooleanListener {
    /**
     * Sets the boolean states of the given widget name
     * */
    fun onBooleanChanged(bl: Boolean, name: String)
}