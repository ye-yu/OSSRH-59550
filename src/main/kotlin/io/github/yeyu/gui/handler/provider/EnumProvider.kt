package io.github.yeyu.gui.handler.provider

import kotlin.reflect.KClass

interface EnumProvider {
    /**
     * Returns the relevant value based on the name
     * of the widget
     * */
    fun <T : Enum<T>> getEnum(name: String, enumClass: KClass<Enum<T>>): T // todo: check
}