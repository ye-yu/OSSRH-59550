package io.github.yeyu.gui.handler.listener

import kotlin.reflect.KClass

// todo: review
interface EnumListener {
    /**
     * Set the enum value of the given widget name
     * */
    fun <T : Enum<T>> onEnumChanged(enum: Enum<T>, name: KClass<T>)
}