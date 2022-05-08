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


private const val VELOCITY_THRESHOLD = 3000
private const val DOUBLE_SWIPE_THRESHOLD = 100
private const val NONE = 0
private const val SWIPE = 1
private const val SWIPE_THREE = 2

class MyViewGroup @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private var mode = NONE
    private var startY = 0f
    private var startX = 0f
    private var stopY = 0f
    private var stopX = 0f

    private val doubleTapMediaPlayer: MediaPlayer by lazy {
        MediaPlayer.create(context, R.raw.double_tap)
    }

    private val singleTapMediaPlayer: MediaPlayer by lazy {
        MediaPlayer.create(context, R.raw.single_tap)
    }

    private val exploringMediaPlayer: MediaPlayer by lazy {
        MediaPlayer.create(context, R.raw.wind)
    }

    private val swipeRightMediaPlayer: MediaPlayer by lazy {
        MediaPlayer.create(context, R.raw.swipe_right)
    }

    private val swipeLeftMediaPlayer: MediaPlayer by lazy {
        MediaPlayer.create(context, R.raw.swipe_left)
    }

    private val swipeUpMediaPlayer: MediaPlayer by lazy {
        MediaPlayer.create(context, R.raw.swipe_up)
    }

    private val swipeDownMediaPlayer: MediaPlayer by lazy {
        MediaPlayer.create(context, R.raw.swipe_down)
    }

    private val swipeRightTwoFingersMediaPlayer: MediaPlayer by lazy {
        MediaPlayer.create(context, R.raw.swipe_right_two_fingers)
    }

    private val swipeLeftTwoFingersMediaPlayer: MediaPlayer by lazy {
        MediaPlayer.create(context, R.raw.swipe_left_two_fingers)
    }

    private val swipeUpTwoFingersMediaPlayer: MediaPlayer by lazy {
        MediaPlayer.create(context, R.raw.swipe_up_two_fingers)
    }

    private val swipeDownTwoFingersMediaPlayer: MediaPlayer by lazy {
        MediaPlayer.create(context, R.raw.swipe_down_two_fingers)
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

        override fun onDown(event: MotionEvent): Boolean {
            logcat { "" }
            logcat { event.description("Down") }
            return false
        }

        override fun onFling(
            event1: MotionEvent, event2: MotionEvent, velocityX: Float,
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
                    swipeRightMediaPlayer.start()
                } else {//if velocityX is negative, then it's towards left
                    logcat { "swipe left" }
                    swipeLeftMediaPlayer.start()
                }
            } else {
                if (velocityY >= 0) {
                    logcat { "swipe down" }
                    swipeDownMediaPlayer.start()
                } else {
                    logcat { "swipe up" }
                    swipeUpMediaPlayer.start()
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

    override fun onInterceptTouchEvent(event: MotionEvent): Boolean = detectEvents(event)

/*    override fun onHoverEvent(event: MotionEvent): Boolean {
        logcat { "onHoverEvent: ${event.action}" }
        //Move AccessibilityManager object to the constructor
        val am = context.getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager
        return if (am.isTouchExplorationEnabled) {
            detectEvents(event)
        } else {
            super.onHoverEvent(event)
        }
    }*/

    private fun detectEvents(event: MotionEvent): Boolean {
        val eventDescription = event.singleTouchDescription()
        logcat { eventDescription }
        when (event.pointerCount) {
            // two fingers
            2 -> {
                when (event.action and MotionEvent.ACTION_MASK) {
                    MotionEvent.ACTION_POINTER_DOWN -> {
                        // This happens when you touch the screen with two fingers
                        mode = SWIPE
                        startY = event.getY(0)
                        startX = event.getX(0)
                    }
                    MotionEvent.ACTION_POINTER_UP -> {
                        // This happens when you release the second finger
                        mode = NONE

                        if (abs(startX - stopX) > abs(startY - stopY)) {
                            if (startX > stopX) {
                                logcat { "double swipe left" }
                                swipeLeftTwoFingersMediaPlayer.start()
                            } else {
                                logcat { "double swipe right" }
                                swipeRightTwoFingersMediaPlayer.start()
                            }

                        } else if (abs(startY - stopY) > DOUBLE_SWIPE_THRESHOLD) {
                            if (startY > stopY) {
                                logcat { "double swipe up" }
                                swipeUpTwoFingersMediaPlayer.start()
                            } else {
                                logcat { "double swipe down" }
                                swipeDownTwoFingersMediaPlayer.start()
                            }
                        }

                        mode = NONE
                    }
                    MotionEvent.ACTION_MOVE -> if (mode == SWIPE) {
                        stopY = event.getY(0)
                        stopX = event.getX(0)
                    }
                }
            }
            1 -> {
                //this is single swipe, I have implemented onFling() here
                gestureDetector.onTouchEvent(event)
            }
            3 -> {
                when (event.action and MotionEvent.ACTION_MASK) {
                    MotionEvent.ACTION_POINTER_DOWN -> {
                        // This happens when you touch the screen with two fingers
                        mode = SWIPE_THREE
                        startY = event.getY(0)
                        startX = event.getX(0)
                    }
                    MotionEvent.ACTION_POINTER_UP -> {
                        // This happens when you release the second finger
                        mode = NONE

                        if (abs(startX - stopX) > abs(startY - stopY)) {
                            if (startX > stopX) {
                                logcat { "triple swipe left" }
                            } else {
                                logcat { "triple swipe right" }
                            }

                        } else if (abs(startY - stopY) > DOUBLE_SWIPE_THRESHOLD) {
                            if (startY > stopY) {
                                logcat { "triple swipe up" }
                            } else {
                                logcat { "triple swipe down" }
                            }
                        }

                        mode = NONE
                    }
                    MotionEvent.ACTION_MOVE -> if (mode == SWIPE_THREE) {
                        stopY = event.getY(0)
                        stopX = event.getX(0)
                    }
                }
            }
        }

        when (event.action) {
            MotionEvent.ACTION_MOVE -> {
                exploringMediaPlayer.start()
            }
            MotionEvent.ACTION_UP -> {
                exploringMediaPlayer.pause()
                exploringMediaPlayer.seekTo(0)
            }
        }

//        gestureDetector.onTouchEvent(event)

        return false
    }

    fun Float.round(): Int {
        return this.toInt()
    }
}
