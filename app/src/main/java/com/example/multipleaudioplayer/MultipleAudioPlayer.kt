package com.example.multipleaudioplayer

import android.app.Application
import logcat.AndroidLogcatLogger
import logcat.LogPriority

class MultipleAudioPlayer : Application() {
    override fun onCreate() {
        super.onCreate()

        AndroidLogcatLogger.installOnDebuggableApp(this, minPriority = LogPriority.VERBOSE)
    }
}