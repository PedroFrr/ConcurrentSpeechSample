package com.example.multipleaudioplayer.gestures

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import androidx.core.view.GestureDetectorCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.multipleaudioplayer.R
import com.example.multipleaudioplayer.databinding.LayoutHomescreenSecondPageBinding
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding
import kotlin.math.abs


class HomeScreenSecondPageFragment : Fragment(R.layout.layout_homescreen_second_page) {

    private val binding by viewBinding(LayoutHomescreenSecondPageBinding::bind)

    private val listAdapter by lazy { DailyAllowanceSummaryListAdapter() }

    private lateinit var mDetector: GestureDetectorCompat

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUi()
        setupGestureDetector()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupUi() {
        setupListAdapter()

        listAdapter.submitList(icons)

        binding.rvHomescreen.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(p0: View?, p1: MotionEvent?): Boolean {
                return mDetector.onTouchEvent(p1)
            }

        })
    }

    private fun setupGestureDetector() {
        mDetector = GestureDetectorCompat(requireContext(), MyGestureListener())
    }

    private fun setupListAdapter() {
        binding.rvHomescreen.apply {
            adapter = listAdapter
            layoutManager = GridLayoutManager(requireContext(), 3)
            hasFixedSize()
        }
    }

    private class MyGestureListener : GestureDetector.SimpleOnGestureListener() {

        override fun onDown(event: MotionEvent): Boolean {
            Log.d(DEBUG_TAG, "onDown: $event")
            return true
        }

        override fun onFling(
            e1: MotionEvent,
            e2: MotionEvent,
            velocityX: Float,
            velocityY: Float
        ): Boolean {
            Log.d(DEBUG_TAG, "onFling: $e1 $e2")
            try {
                if (abs(e1.y - e2.y) > SWIPE_MAX_OFF_PATH) return false
                if (e1.x - e2.x > SWIPE_MIN_DISTANCE && abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                    Log.i(DEBUG_TAG, "Right to Left")
                } else if (e2.x - e1.x > SWIPE_MIN_DISTANCE && abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                    Log.i(DEBUG_TAG, "Left to Right")
                }
            } catch (e: Exception) {
                // nothing
            }
            return super.onFling(e1, e2, velocityX, velocityY)
        }
    }

    companion object {
        const val DEBUG_TAG = "debug_tag"
        const val SWIPE_MIN_DISTANCE = 120
        const val SWIPE_MAX_OFF_PATH = 250
        const val SWIPE_THRESHOLD_VELOCITY = 200
        val icons = listOf(
            "Elemento 1",
            "Elemento 2",
            "Elemento 3",
            "Elemento 4",
            "Elemento 5",
            "Elemento 6",
            "Elemento 7",
            "Elemento 8",
            "Elemento 9"
        )
    }
}