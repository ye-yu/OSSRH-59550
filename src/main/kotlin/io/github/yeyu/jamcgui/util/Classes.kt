package io.github.yeyu.jamcgui.util

import java.util.function.Consumer
import java.util.function.Function
import kotlin.reflect.KClass
import kotlin.reflect.full.cast
import java.lang.ClassCastException as JClassCastException

object Classes {

    /**
     * @throws java.lang.ClassCastException the passed parameter is not an instance of specified class
     * */
    fun requireInstanceOf(obj: Any, clazz: Class<*>) {
        if (!clazz.isInstance(obj)) throw JClassCastException("${obj.javaClass.simpleName} is not a type of ${clazz.simpleName}")
    }

    /**
     * @throws ClassCastException the passed parameter is not an instance of specified class
     * */
    fun requireInstanceOf(obj: Any, clazz: KClass<*>) {
        if (!clazz.isInstance(obj)) throw ClassCastException("${obj.javaClass.simpleName} is not a type of ${clazz.simpleName}")
    }

    /**
     * Runs consumer lambda if the passed `obj` is an instance of the specified class.
     *
     * Otherwise, logs `failedMsg` error.
     *
     * Example,
     *
     * ```java
     * runUnsafe(obj, EventListener.class, null, castedObj -> {
     *     castedObj.onEventChanged();
     *     return null;
     * });
     * ```
     * */
    fun <T> runUnsafe(
        obj: Any,
        castTo: Class<T>,
        failedMsg: String?,
        consumer: Consumer<T>
    ) {
        if (!castTo.isInstance(obj)) {
            if (failedMsg != null)
                Logger.error(failedMsg, Throwable())
            return
        }
        consumer.accept(castTo.cast(obj))
    }

    /**
     * Runs consumer lambda if the passed `obj` is an instance of the specified class.
     *
     * Otherwise, logs `failedMsg` error.
     *
     * Example,
     *
     * ```kotlin
     * runUnsafe(obj, EventListener::class, null) { it.onEventChanged() }
     * ```
     * */
    fun <T : Any> runUnsafe(obj: Any, castTo: KClass<T>, failedMsg: String?, consumer: (T) -> Unit) {
        if (!castTo.isInstance(obj)) {
            if (failedMsg != null)
                Logger.error(failedMsg, Throwable())
            return
        }
        consumer(castTo.cast(obj))
    }

    /**
     * Runs lambda and returns the returned value if `obj` is an instance
     * of the specified class.
     *
     * Otherwise, returns `fallback`.
     * */
    fun <T, V> getUnsafe(
        obj: Any,
        castTo: Class<T>,
        fallback: V,
        function: Function<T, V>
    ): V {
        if (!castTo.isInstance(obj)) return fallback
        return function.apply(castTo.cast(obj))
    }

    /**
     * Runs lambda and returns the returned value if `obj` is an instance
     * of the specified class.
     *
     * Otherwise, returns `fallback`.
     *
     * Example,
     *
     * ```kotlin
     * getUnsafe(boolType, BooleanProvider::class, false) { it.getBoolean() }
     * ```
     * */
    fun <T : Any, V> getUnsafe(obj: Any, castTo: KClass<T>, fallback: V, consumer: (T) -> V): V {
        if (!castTo.isInstance(obj)) return fallback
        return consumer(castTo.cast(obj))
    }
}