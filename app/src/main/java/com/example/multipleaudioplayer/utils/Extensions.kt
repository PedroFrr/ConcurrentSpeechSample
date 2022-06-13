package com.example.multipleaudioplayer.utils

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.annotation.AnyRes
import androidx.fragment.app.Fragment

/**
 * @param speedRate 100 is the default rate, 50 is half the speed and 200 is twice the speed
 * @param pitch 0 is the default rate
 */
fun String.convertToSsml(speedRate: Float = 100f, pitch: Float = 0f, timbre: Int = 100): String =
    "<speak> <amazon:effect vocal-tract-length=\"$timbre%\"><prosody rate=\"$speedRate%\" pitch=\"$pitch%\"> $this </prosody></amazon:effect> </speak>"

fun Fragment.showToast(message: String) {
    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
}

internal fun Context.getResourceUri(@AnyRes resourceId: Int): Uri =
    Uri.Builder()
        .scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
        .authority(packageName)
        .path(resourceId.toString())
        .build()