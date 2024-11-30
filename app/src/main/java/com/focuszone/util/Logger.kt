package com.focuszone.util

import android.util.Log

object Logger {

    // enable/disable
    var isLoggingEnabled: Boolean = true

    // log certain value
    fun logValue(tag: String, variableName: String, value: Any?) {
        if (isLoggingEnabled) {
            val message = "Variable [$variableName]: $value"
            Log.d(tag, message)
        }
    }

    // DEBUG
    fun debug(tag: String, message: String) {
        if (isLoggingEnabled) {
            Log.d(tag, message)
        }
    }

    // INFO
    fun info(tag: String, message: String) {
        if (isLoggingEnabled) {
            Log.i(tag, message)
        }
    }

    // WARN
    fun warn(tag: String, message: String) {
        if (isLoggingEnabled) {
            Log.w(tag, message)
        }
    }

    // ERROR
    fun error(tag: String, message: String, throwable: Throwable? = null) {
        if (isLoggingEnabled) {
            if (throwable != null) {
                Log.e(tag, message, throwable)
            } else {
                Log.e(tag, message)
            }
        }
    }
}
