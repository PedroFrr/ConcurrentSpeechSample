package com.example.multipleaudioplayer.customviews

import android.annotation.SuppressLint
import android.content.Context
import android.media.MediaPlayer
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ViewConfiguration
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.multipleaudioplayer.R
import logcat.logcat

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

    private val gestureListener = object : GestureDetector.SimpleOnGestureListener() {
        override fun onShowPress(event: MotionEvent?) {
            logcat { event.description("Press") }
        }

        override fun onSingleTapUp(event: MotionEvent?): Boolean {
            logcat { event.description("Single tap up") }
            return true
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
            logcat { event1.description("Fling start") }
            logcat { event2.description("Fling end") }
            return true
        }

        override fun onScroll(
            event1: MotionEvent?, event2: MotionEvent?, distanceX: Float,
            distanceY: Float
        ): Boolean {
            logcat { event2.description("Scroll") }
            logcat { "Scroll distance (${distanceX.toInt()}, ${distanceY.toInt()})" }
            return true
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
            return true
        }

        override fun onContextClick(event: MotionEvent?): Boolean {
            logcat { event.description("Context click") }
            return true
        }

        fun MotionEvent?.description(description: String): String {
            return if (this == null) "Empty press" else "$description at (${x.round()}, ${y.round()})"
        }
    }

    private val gestureDetector = GestureDetector(context, gestureListener)

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        gestureDetector.onTouchEvent(ev)

        return false
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        // Here we actually handle the touch event (e.g. if the action is ACTION_MOVE,
        // scroll this container).
        // This method will only be called if the touch event was intercepted in
        // onInterceptTouchEvent
        return false
    }

    fun Float.round(): Int {
        return this.toInt()
    }
}
