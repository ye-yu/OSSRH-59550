package io.github.yeyu.util

import kotlin.reflect.KClass
import kotlin.reflect.full.cast

object Classes {
    fun requireInstanceOf(obj: Any, clazz: Class<*>) {
        if (!clazz.isInstance(obj)) throw ClassCastException("${obj.javaClass.simpleName} is not a type of ${clazz.simpleName}")
    }

    fun requireInstanceOf(obj: Any, clazz: KClass<*>) {
        if (!clazz.isInstance(obj)) throw ClassCastException("${obj.javaClass.simpleName} is not a type of ${clazz.simpleName}")
    }

    fun <T> runUnsafe(obj: Any, castTo: Class<T>, failedMsg: String?, consumer: (T) -> Unit) {
        if (!castTo.isInstance(obj)) {
            if (failedMsg != null)
            Logger.error(failedMsg, Throwable())
            return
        }
        consumer(castTo.cast(obj))
    }

    fun <T: Any> runUnsafe(obj: Any, castTo: KClass<T>, failedMsg: String?, consumer: (T) -> Unit) {
        if (!castTo.isInstance(obj)) {
            if (failedMsg != null)
                Logger.error(failedMsg, Throwable())
            return
        }
        consumer(castTo.cast(obj))
    }

    fun <T, V> getUnsafe(obj: Any, castTo: Class<T>, fallback: V, consumer: (T) -> V): V {
        if (!castTo.isInstance(obj)) return fallback
        return consumer(castTo.cast(obj))
    }

    fun <T: Any, V> getUnsafe(obj: Any, castTo: KClass<T>, fallback: V, consumer: (T) -> V): V {
        if (!castTo.isInstance(obj)) return fallback
        return consumer(castTo.cast(obj))
    }
}