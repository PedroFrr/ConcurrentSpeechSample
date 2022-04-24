package com.example.multipleaudioplayer.customviews

import android.content.Context
import android.media.MediaPlayer
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.multipleaudioplayer.R
import com.example.multipleaudioplayer.singleTouchDescription
import logcat.logcat
import kotlin.math.abs

const val VELOCITY_THRESHOLD = 3000

class MyViewGroup @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private val doubleTapMediaPlayer: MediaPlayer by lazy {
        MediaPlayer.create(context, R.raw.double_tap)
    }

    private val singleTapMediaPlayer: MediaPlayer by lazy {
        MediaPlayer.create(context, R.raw.single_tap)
    }

    private val exploringMediaPlayer: MediaPlayer by lazy {
        MediaPlayer.create(context, R.raw.wind)
    }

    private val gestureListener = object : GestureDetector.SimpleOnGestureListener() {
        override fun onShowPress(event: MotionEvent?) {
            logcat { event.description("Press") }
        }

        override fun onSingleTapUp(event: MotionEvent?): Boolean {
            logcat { event.description("Single tap up") }
            return false
        }

        override fun onSingleTapConfirmed(event: MotionEvent?): Boolean {
            logcat { event.description("Single tap confirmed") }
            singleTapMediaPlayer.start()
            return false
        }

        override fun onDown(event: MotionEvent?): Boolean {
            logcat { "" }
            logcat { event.description("Down") }
            return false
        }

        override fun onFling(
            event1: MotionEvent?, event2: MotionEvent?, velocityX: Float,
            velocityY: Float
        ): Boolean {
            if (abs(velocityX) < VELOCITY_THRESHOLD
                && abs(velocityY) < VELOCITY_THRESHOLD
            ) {
                return false //if the fling is not fast enough then it's just like drag
            }

            //if velocity in X direction is higher than velocity in Y direction,
            //then the fling is horizontal, else->vertical
            if (abs(velocityX) > abs(velocityY)) {
                if (velocityX >= 0) {
                    logcat { "swipe right" }
                } else {//if velocityX is negative, then it's towards left
                    logcat { "swipe left" }
                }
            } else {
                if (velocityY >= 0) {
                    logcat { "swipe down" }
                } else {
                    logcat { "swipe up" }
                }
            }

            return false
        }

        override fun onScroll(
            event1: MotionEvent?, event2: MotionEvent?, distanceX: Float,
            distanceY: Float
        ): Boolean {
            logcat { event2.description("Scroll") }
            logcat { "Scroll distance (${distanceX.toInt()}, ${distanceY.toInt()})" }
            return false
        }

        override fun onLongPress(event: MotionEvent?) {
            logcat { event.description("Long press") }
        }

        override fun onDoubleTap(event: MotionEvent?): Boolean {
            logcat { event.description("Double tap") }
            doubleTapMediaPlayer.start()
            return false
        }

        override fun onDoubleTapEvent(event: MotionEvent?): Boolean {
            logcat { event.description("Double tap event") }
            return false
        }

        override fun onContextClick(event: MotionEvent?): Boolean {
            logcat { event.description("Context click") }
            return false
        }

        fun MotionEvent?.description(description: String): String {
            return if (this == null) "Empty press" else "$description at (${x.round()}, ${y.round()})"
        }
    }

    private val gestureDetector = GestureDetector(context, gestureListener)

    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
        val eventDescription = event.singleTouchDescription()
        logcat { eventDescription }
        when (event.action) {
            MotionEvent.ACTION_MOVE -> {
                exploringMediaPlayer.start()
            }
            MotionEvent.ACTION_UP -> {
                exploringMediaPlayer.pause()
                exploringMediaPlayer.seekTo(0)
            }
        }

        gestureDetector.onTouchEvent(event)

        return false
    }

    fun Float.round(): Int {
        return this.toInt()
    }
}
