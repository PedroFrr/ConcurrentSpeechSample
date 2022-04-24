package com.example.multipleaudioplayer.gestures

import android.annotation.SuppressLint
import android.media.MediaPlayer
import android.os.Bundle
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.example.multipleaudioplayer.R
import com.example.multipleaudioplayer.databinding.LayoutGesturesExampleBinding
import com.example.multipleaudioplayer.singleTouchDescription
import com.example.multipleaudioplayer.utils.showToast
import com.google.android.material.tabs.TabLayoutMediator
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import logcat.logcat

private const val NUM_PAGES = 2

class GesturesExampleFragment : Fragment(R.layout.layout_gestures_example) {

    private val binding by viewBinding(LayoutGesturesExampleBinding::bind)

    private val scope = CoroutineScope(Dispatchers.IO)

    private lateinit var viewPager: ViewPager2

    private var currentPage = 0

    private val swipeRight: MediaPlayer by lazy {
        MediaPlayer.create(requireActivity(), R.raw.swipe_right)
    }

    private val swipeLeft: MediaPlayer by lazy {
        MediaPlayer.create(requireActivity(), R.raw.swipe_left)
    }

    private val loadingMediaPlayer: MediaPlayer by lazy {
        MediaPlayer.create(requireActivity(), R.raw.loading)
    }

    private val exploringMediaPlayer: MediaPlayer by lazy {
        MediaPlayer.create(requireActivity(), R.raw.wind)
    }

    private val singleTapMediaPlayer: MediaPlayer by lazy {
        MediaPlayer.create(requireActivity(), R.raw.single_tap)
    }

    private val doubleTapMediaPlayer: MediaPlayer by lazy {
        MediaPlayer.create(requireActivity(), R.raw.double_tap)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUi()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupUi() {
        // Instantiate a ViewPager2 and a PagerAdapter.
        viewPager = binding.pager

        // The pager adapter, which provides the pages to the view pager widget.
        val pagerAdapter = ScreenSlidePagerAdapter(requireActivity())
        viewPager.adapter = pagerAdapter

        val tabLayout = binding.tabLayout
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = "Página ${(position + 1)}"
        }.attach()

        viewPager.registerOnPageChangeCallback(onPageListener)

        binding.srlHomescreen.setOnRefreshListener {
            showToast("Updating............")

            scope.launch {
                loadingMediaPlayer.start()
                withContext(Dispatchers.Main) {
                    binding.overlay.visibility = View.VISIBLE
                    delay(6000)
                    binding.srlHomescreen.isRefreshing = false
                    binding.overlay.visibility = View.GONE
                }
            }
        }

        binding.clContainer.setOnTouchListener { _, event ->
            val eventDescription = event.singleTouchDescription()
            logcat { eventDescription }
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    exploringMediaPlayer.start()
                    return@setOnTouchListener true
                }
                MotionEvent.ACTION_MOVE -> {
                    return@setOnTouchListener true
                }
                MotionEvent.ACTION_UP -> {
                    exploringMediaPlayer.pause()
                    exploringMediaPlayer.seekTo(0)
                    return@setOnTouchListener true
                }
                else -> {
                    return@setOnTouchListener true
                }
            }
        }

        setUpGestureDetector()

    }

    private val onPageListener = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            if (currentPage != position) {
                when (position) {
                    0 -> swipeLeft.start()
                    1 -> swipeRight.start()
                }
                currentPage = position
            }

            super.onPageSelected(position)
        }
    }

    @SuppressLint("ClickableViewAccessibility", "SetTextI18n")
    private fun setUpGestureDetector() {
        /*val gestureListener = object : GestureDetector.SimpleOnGestureListener() {
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
                return true
            }

            override fun onFling(
                event1: MotionEvent?, event2: MotionEvent?, velocityX: Float,
                velocityY: Float
            ): Boolean {
                logcat { event1.description("Fling start") }
                logcat { event2.description("Fling end") }
                logcat { "Fling velocity (${velocityX.round()}px/s, ${velocityY.round()}px/s)" }
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
                return true
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

        val gestureDetector = GestureDetector(requireContext(), gestureListener)

        binding.clContainer.setOnTouchListener { _, event ->
            gestureDetector.onTouchEvent(event)
        }*/
    }

    fun Float.round(): Int {
        return this.toInt()
    }

    override fun onDestroy() {
        viewPager.unregisterOnPageChangeCallback(onPageListener)
        super.onDestroy()
    }

    /**
     * A simple pager adapter that represents 5 ScreenSlidePageFragment objects, in
     * sequence.
     */
    private inner class ScreenSlidePagerAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {
        override fun getItemCount(): Int = NUM_PAGES

        override fun createFragment(position: Int): Fragment {
            val fragment = when (position) {
                0 -> HomeScreenFirstPageFragment()
                else -> HomeScreenSecondPageFragment()
            }
            return fragment
        }
    }
}