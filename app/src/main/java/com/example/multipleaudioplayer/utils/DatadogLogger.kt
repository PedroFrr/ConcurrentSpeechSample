package com.example.multipleaudioplayer.utils

import com.datadog.android.log.Logger

object DatadogLogger {
    private val logger = Logger.Builder()
        .setNetworkInfoEnabled(true)
        .setLogcatLogsEnabled(true)
        .setDatadogLogsEnabled(true)
        .setBundleWithTraceEnabled(true)
        .setLoggerName("Concurrent")
        .build()

    fun logInfo(message: String) = logger.i(message)
}