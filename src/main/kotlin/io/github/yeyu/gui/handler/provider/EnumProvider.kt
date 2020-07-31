package io.github.yeyu.gui.handler.provider

interface EnumProvider {
    fun <T: Enum<T>> getEnum(name: String): T
}