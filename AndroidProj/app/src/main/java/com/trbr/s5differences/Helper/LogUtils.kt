package com.trbr.s5differences.Helper

import android.util.Log

object LogUtils {
    var className: String = "null"
    var lineNumber: Int = 0
    var methodName: String = "null"

    private fun getMethodNames(var0: Array<StackTraceElement>) {
        className = var0[1].fileName
        methodName = var0[1].methodName
        lineNumber = var0[1].lineNumber
    }

    private fun createLog(msg: String): String {
        return "================${"$methodName($className:$lineNumber)"}================:$msg"
    }

    private const val isDebuggable = true
//    private const val isDebuggable = true

    fun d(msg: String) {
        if (isDebuggable) {
            getMethodNames(Throwable().stackTrace)
            Log.d(className, createLog(msg))
        }
    }

    @JvmStatic
    fun e(msg: String) {
        if (isDebuggable) {
            getMethodNames(Throwable().stackTrace)
            Log.e(className, createLog(msg))
        }
    }

    @JvmStatic
    fun i(msg: String) {
        if (isDebuggable) {
            getMethodNames(Throwable().stackTrace)
            Log.i(className, createLog(msg))
        }
    }

    fun v(msg: String) {
        if (isDebuggable) {
            getMethodNames(Throwable().stackTrace)
            Log.v(className, createLog(msg))
        }
    }

    fun w(msg: String) {
        if (isDebuggable) {
            getMethodNames(Throwable().stackTrace)
            Log.w(className, createLog(msg))
        }
    }
}


