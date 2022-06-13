package com.example.multipleaudioplayer.utils

import androidx.fragment.app.Fragment
import androidx.navigation.NavDestination
import com.datadog.android.rum.tracking.ComponentPredicate
import com.example.multipleaudioplayer.audioproperties.AudioPropertiesEarconsExampleFragment
import com.example.multipleaudioplayer.audioproperties.AudioPropertiesSamplesExampleFragment
import com.example.multipleaudioplayer.gestures.HomeScreenFirstPageFragment
import com.example.multipleaudioplayer.gestures.HomeScreenSecondPageFragment
import com.example.multipleaudioplayer.notification.nospatialization.NotificationNoSpatializationExampleFragment
import com.example.multipleaudioplayer.notification.spatialization.NotificationSpatializationExampleFragment
import com.example.multipleaudioplayer.scanning.ScanningExampleFragment
import com.example.multipleaudioplayer.scanning.nospatialization.ScanningNoSpatializationExampleFragment
import com.example.multipleaudioplayer.scanning.spatialization.ScanningSpatializationExampleFragment
import com.example.multipleaudioplayer.spatialization.SpatializationFirstNewsExampleFragment
import com.example.multipleaudioplayer.spatialization.SpatializationSecondNewsExampleFragment
import logcat.logcat

class SampleNavigationPredicate : ComponentPredicate<Fragment> {
    override fun accept(component: Fragment): Boolean {
        return true
    }

    override fun getViewName(component: Fragment): String? {
        return when(component.javaClass) {
            ScanningSpatializationExampleFragment::class.java -> "Scanning"
            ScanningNoSpatializationExampleFragment::class.java -> "Scanning"
            NotificationSpatializationExampleFragment::class.java -> "Notification"
            NotificationNoSpatializationExampleFragment::class.java -> "Notification"
            SpatializationFirstNewsExampleFragment::class.java -> "Spatialization"
            SpatializationSecondNewsExampleFragment::class.java -> "Spatialization"
            AudioPropertiesSamplesExampleFragment::class.java -> "Audio properties"
            AudioPropertiesEarconsExampleFragment::class.java -> "Audio properties"
            HomeScreenFirstPageFragment::class.java -> "Homescreen"
            HomeScreenSecondPageFragment::class.java -> "Homescreen"
            else -> null // use the default view name created automatically
        }
    }
}