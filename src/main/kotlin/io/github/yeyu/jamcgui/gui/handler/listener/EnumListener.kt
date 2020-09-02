package io.github.yeyu.jamcgui.gui.handler.listener

interface EnumListener {
    /**
     * Set the enum value of the given widget name
     * */
    fun onEnumChanged(enum: Enum<*>, name: String)
}