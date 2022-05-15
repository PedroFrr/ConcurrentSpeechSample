package com.example.multipleaudioplayer.utils

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlin.time.Duration

class Utils {
    companion object{
        fun tickerFlow(period: Duration, initialDelay: Duration = Duration.ZERO) = flow {
            delay(initialDelay)
            while (true) {
                emit(Unit)
                delay(period)
            }
        }
    }
}