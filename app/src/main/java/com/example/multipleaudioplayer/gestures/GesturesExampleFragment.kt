package com.example.multipleaudioplayer.gestures

import android.annotation.SuppressLint
import android.media.MediaPlayer
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.view.ViewGroup
import android.view.accessibility.AccessibilityEvent
import androidx.core.view.AccessibilityDelegateCompat
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.example.multipleaudioplayer.R
import com.example.multipleaudioplayer.databinding.LayoutGesturesExampleBinding
import com.example.multipleaudioplayer.utils.showToast
import com.google.android.material.tabs.TabLayoutMediator
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import logcat.logcat

private const val NUM_PAGES = 2

class GesturesExampleFragment : Fragment(R.layout.layout_gestures_example) {

    private val binding by viewBinding(LayoutGesturesExampleBinding::bind)

    private val scope = CoroutineScope(Dispatchers.IO)

    private lateinit var viewPager: ViewPager2

    private var job: Job? = null

    private val loadingMediaPlayer: MediaPlayer by lazy {
        MediaPlayer.create(requireActivity(), R.raw.loading)
    }

    private val exploringMediaPlayer: MediaPlayer by lazy {
        MediaPlayer.create(context, R.raw.wind)
    }

    private val imageViewMediaPlayer: MediaPlayer by lazy {
        MediaPlayer.create(context, R.raw.image_view)
    }

    private val textViewMediaPlayer: MediaPlayer by lazy {
        MediaPlayer.create(context, R.raw.text_view)
    }

    private val editTextMediaPlayer: MediaPlayer by lazy {
        MediaPlayer.create(context, R.raw.edit_text)
    }

    private val switchMediaPlayer: MediaPlayer by lazy {
        MediaPlayer.create(context, R.raw.switch_sound)
    }

    private val radioButtonMediaPlayer: MediaPlayer by lazy {
        MediaPlayer.create(context, R.raw.radio_button)
    }

    private val checkboxMediaPlayer: MediaPlayer by lazy {
        MediaPlayer.create(context, R.raw.check_box)
    }

    private val buttonMediaPlayer: MediaPlayer by lazy { MediaPlayer.create(binding.root.context, R.raw.key_press) }

    private val singleTapMediaPlayer: MediaPlayer by lazy {
        MediaPlayer.create(context, R.raw.single_tap)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUi()

        binding.srlHomescreen.isEnabled = false
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

        ViewCompat.setAccessibilityDelegate(binding.root, object : AccessibilityDelegateCompat() {
            override fun onRequestSendAccessibilityEvent(host: ViewGroup?, child: View?, event: AccessibilityEvent): Boolean {
                logcat { event.toString() }
                noGesture.cancel()
                when (event.eventType) {
                    AccessibilityEvent.TYPE_VIEW_ACCESSIBILITY_FOCUSED -> {
                        timer.cancel()
                        noGesture.start()
                    }
                    AccessibilityEvent.TYPE_VIEW_HOVER_EXIT -> {
                        timer.start()
                    }
                    AccessibilityEvent.TYPE_VIEW_HOVER_ENTER -> {
                        singleTapMediaPlayer.start() // single tap sound (with talkback on)
                        timer.cancel()
                        when (event.className) {
                            Widget.BUTTON.value -> {
                                buttonMediaPlayer.start()
                            }
                            Widget.IMAGE_VIEW.value -> {
                                imageViewMediaPlayer.start()
                            }
                            Widget.TEXT_VIEW.value -> {
                                textViewMediaPlayer.start()
                            }
                            Widget.EDIT_TEXT.value -> {
                                editTextMediaPlayer.start()
                            }
                            Widget.SWITCH.value -> {
                                switchMediaPlayer.start()
                            }
                            Widget.RADIO_BUTTON.value -> {
                                radioButtonMediaPlayer.start()
                            }
                            Widget.CHECK_BOX.value -> {
                                checkboxMediaPlayer.start()
                            }

                        }
                        if (!exploringMediaPlayer.isPlaying) exploringMediaPlayer.start()
                    }
                }
                return super.onRequestSendAccessibilityEvent(host, child, event)
            }
        })
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

    val timer = object : CountDownTimer(100, 100) {
        override fun onTick(p0: Long) {
            // do nothing
        }

        override fun onFinish() {
            exploringMediaPlayer.pause()
            exploringMediaPlayer.seekTo(0)
        }
    }

    val noGesture = object : CountDownTimer(1000, 100) {
        override fun onTick(p0: Long) {
            // do nothing
        }

        override fun onFinish() {
            exploringMediaPlayer.pause()
            exploringMediaPlayer.seekTo(0)
        }
    }

/*    companion object {
        private const val BUTTON = "android.widget.Button"
        private const val IMAGE_VIEW = "android.widget.ImageView"
        private const val TEXT_VIEW = "android.widget.TextView"
        private const val EDIT_TEXT = "android.widget.EditText"
        private const val SWITCH = "android.widget.Switch"
        private const val RADIO_BUTTON = "android.widget.RadioButton"
        private const val CHECK_BOX = "android.widget.CheckBox"
    }*/

    enum class Widget(val value: String) {
        BUTTON("android.widget.Button"),
        IMAGE_VIEW("android.widget.ImageView"),
        TEXT_VIEW("android.widget.TextView"),
        EDIT_TEXT("android.widget.EditText"),
        SWITCH("android.widget.Switch"),
        RADIO_BUTTON("android.widget.RadioButton"),
        CHECK_BOX("android.widget.CheckBox"),
    }
}