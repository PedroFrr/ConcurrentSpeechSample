package com.example.multipleaudioplayer.utils

/**
 * @param speedRate 100 is the default rate, 50 is half the speed and 200 is twice the speed
 * @param pitch 0 is the default rate
 */
fun String.convertToSsml(speedRate: Float = 100f, pitch: Float = 0f, timbre: Int = 100): String =
    "<speak> <amazon:effect vocal-tract-length=\"$timbre%\"><prosody rate=\"$speedRate%\" pitch=\"$pitch%\"> $this </prosody></amazon:effect> </speak>"
