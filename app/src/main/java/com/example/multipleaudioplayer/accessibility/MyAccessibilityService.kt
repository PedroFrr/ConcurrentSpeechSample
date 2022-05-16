package com.example.multipleaudioplayer.accessibility

import android.accessibilityservice.AccessibilityGestureEvent
import android.accessibilityservice.AccessibilityService
import android.os.Build
import android.view.accessibility.AccessibilityEvent
import androidx.annotation.RequiresApi
import logcat.logcat

class MyAccessibilityService : AccessibilityService() {
    override fun onInterrupt() {}

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {}

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onGesture(gestureEvent: AccessibilityGestureEvent): Boolean {
        logcat { gestureEvent.gestureId.toString() }
        return false
    }
}