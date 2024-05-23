package com.demo.readwriteexternalstoragepermission.ui.utils

import android.util.Log


/**
 * Created on mayo.
 * year 2024 .
 */
class LoggerUtils {
    fun logError(tag: String, message: String, ex: Exception) {
        Log.e(tag, "$message ${ex.message}")
    }
}