package io.github.yeyu.gui.handler.listener

interface FieldListener {
    fun onFieldChanged(obj: Any, castTo: Class<*>, name: String)
}