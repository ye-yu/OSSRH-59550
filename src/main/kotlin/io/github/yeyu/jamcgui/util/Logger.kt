package io.github.yeyu.jamcgui.util

import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

/**
 * A logger util, so that you don't have
 * to construct static properties for
 * logger for every class
 * */
object Logger {

    private val loggers = HashMap<String, Logger>()
    fun info(msg: String) {
        val stackTrace = Thread.currentThread().stackTrace
        val lastCallingClass = 2
        val className = stackTrace[lastCallingClass.coerceAtMost(stackTrace.size)].className
        if (!loggers.contains(className)) loggers[className] = LogManager.getLogger(className)
        loggers[className]?.info(msg)
    }

    fun error(msg: String, t: Throwable) {
        val stackTrace = Thread.currentThread().stackTrace
        val lastCallingClass = 2
        val className = stackTrace[lastCallingClass.coerceAtMost(stackTrace.size)].className
        if (!loggers.contains(className)) loggers[className] = LogManager.getLogger(className)
        loggers[className]?.error(msg, t)
    }

    fun warn(msg: String) {
        val stackTrace = Thread.currentThread().stackTrace
        val lastCallingClass = 2
        val className = stackTrace[lastCallingClass.coerceAtMost(stackTrace.size)].className
        if (!loggers.contains(className)) loggers[className] = LogManager.getLogger(className)
        loggers[className]?.info(msg)
    }
}
