package io.github.yeyu.gui.handler.listener

import kotlin.reflect.KClass

interface EnumListener {

    fun <T : Enum<T>> onEnumChanged(enum: Enum<T>, name: KClass<T>)
}