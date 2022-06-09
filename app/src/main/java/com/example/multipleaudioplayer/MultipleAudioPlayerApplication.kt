package com.example.multipleaudioplayer

import android.app.Application
import android.util.Log
import com.datadog.android.Datadog
import com.datadog.android.DatadogSite
import com.datadog.android.core.configuration.Configuration
import com.datadog.android.core.configuration.Credentials
import com.datadog.android.privacy.TrackingConsent
import com.datadog.android.rum.GlobalRum
import com.datadog.android.rum.RumMonitor
import com.datadog.android.rum.tracking.FragmentViewTrackingStrategy
import com.datadog.android.tracing.AndroidTracer
import io.opentracing.util.GlobalTracer
import logcat.AndroidLogcatLogger
import logcat.LogPriority

class MultipleAudioPlayerApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        AndroidLogcatLogger.installOnDebuggableApp(this, minPriority = LogPriority.VERBOSE)

        initializeDatadog()
    }

    private fun initializeDatadog() {
        Datadog.initialize(
            this,
            createDatadogCredentials(),
            createDatadogConfiguration(),
            TrackingConsent.GRANTED
        )
        Datadog.setVerbosity(Log.VERBOSE)
        Datadog.enableRumDebugging(true)

        GlobalTracer.registerIfAbsent(AndroidTracer.Builder().build())
        GlobalRum.registerIfAbsent(RumMonitor.Builder().build())
    }

    private fun createDatadogCredentials(): Credentials {
        return Credentials(
            clientToken = "pubda10e33c4f4cb8b12f7b5eff712e6a19",
            envName = BuildConfig.BUILD_TYPE,
            variant = BuildConfig.FLAVOR,
            rumApplicationId = "1094529f-144d-4616-8c2d-dcbe29951a3f"
        )
    }

    private fun createDatadogConfiguration(): Configuration {
        val configBuilder = Configuration.Builder(
            logsEnabled = true,
            tracesEnabled = true,
            crashReportsEnabled = true,
            rumEnabled = true
        )
            .useViewTrackingStrategy(
                FragmentViewTrackingStrategy(true)
            )
            .trackInteractions()
            .trackLongTasks(250L)

        try {
            configBuilder.useSite(DatadogSite.US1)
        } catch (e: IllegalArgumentException) {
            // no-op
        }
        return configBuilder.build()
    }
}