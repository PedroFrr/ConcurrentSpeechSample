package com.example.multipleaudioplayer.accessibility

import android.accessibilityservice.AccessibilityService
import android.util.Log
import android.view.accessibility.AccessibilityEvent

class MyAccessibilityService : AccessibilityService() {
    override fun onAccessibilityEvent(p0: AccessibilityEvent?) {}

    override fun onInterrupt() {}

    override fun onGesture(gestureId: Int): Boolean {
        Log.d("caller", "on gesture called");
        return true
    }
}