package com.example.multipleaudioplayer

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.view.MotionEvent
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

fun Context.flowBroadcastReceiver(filter: IntentFilter): Flow<Intent> {
    return callbackFlow {
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent) {
                trySend(intent)
            }
        }
        registerReceiver(receiver, filter)
        awaitClose {
            unregisterReceiver(receiver)
        }
    }
}

fun MotionEvent.singleTouchDescription(): String {
    val eventLiteral = when (action) {
        MotionEvent.ACTION_DOWN -> "Down"
        MotionEvent.ACTION_UP -> "Up"
        MotionEvent.ACTION_MOVE -> "Move"
        MotionEvent.ACTION_CANCEL -> "Cancel"
        MotionEvent.ACTION_OUTSIDE -> "Outside"
        else -> ""
    }
    return if (eventLiteral.isEmpty()) {
        ""
    } else {
        "$eventLiteral action at (${x.toInt()}, ${y.toInt()})"
    }
}