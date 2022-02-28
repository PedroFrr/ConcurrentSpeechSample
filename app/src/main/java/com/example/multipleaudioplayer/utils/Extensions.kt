package com.example.multipleaudioplayer.utils

/**
 * @param speedRate 100 is the default rate, 50 is half the speed and 200 is twice the speed
 */
fun String.convertToSsml(speedRate: Int = 100): String =
    "<speak> <prosody rate=\"$speedRate%\"> $this </prosody> </speak>"
