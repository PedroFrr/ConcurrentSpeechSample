package com.example.multipleaudioplayer.gestures

import android.R.attr
import android.annotation.SuppressLint
import android.content.Context
import android.media.MediaPlayer
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.accessibility.AccessibilityEvent
import android.view.inputmethod.InputMethodManager
import androidx.core.view.AccessibilityDelegateCompat
import androidx.core.view.ViewCompat
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat.AccessibilityActionCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.multipleaudioplayer.R
import com.example.multipleaudioplayer.databinding.LayoutHomescreenFirstPageBinding
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding
import logcat.logcat


class HomeScreenFirstPageFragment : Fragment(R.layout.layout_homescreen_first_page) {

    private val binding by viewBinding(LayoutHomescreenFirstPageBinding::bind)

    private val listAdapter by lazy { ItemsListAdapter() }

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUi()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupUi() {
        setupListAdapter()

        listAdapter.submitList(icons)

        binding.clContainer.setOnTouchListener { view, _ ->
            val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view?.windowToken, 0)
            true
        }

        binding.apply {
            cb.setOnClickListener {
                checkboxMediaPlayer.start()
            }
            et.setOnClickListener {
                editTextMediaPlayer.start()
            }
            iv.setOnClickListener {
                imageViewMediaPlayer.start()
            }
            rbOption1.setOnClickListener {
                radioButtonMediaPlayer.start()
            }
            rbOption2.setOnClickListener {
                radioButtonMediaPlayer.start()
            }
            sv.setOnClickListener {
                switchMediaPlayer.start()
            }
            tv.setOnClickListener {
                textViewMediaPlayer.start()
            }
        }

        ViewCompat.setAccessibilityDelegate(binding.root, object : AccessibilityDelegateCompat() {
             override fun onInitializeAccessibilityNodeInfo(v: View, info: AccessibilityNodeInfoCompat) {
                 super.onInitializeAccessibilityNodeInfo(v, info)
                 // A custom action description. For example, you could use "pause"
                 // to have TalkBack speak "double-tap to pause."
                 // A custom action description. For example, you could use "pause"
                 // to have TalkBack speak "double-tap to pause."
                 val customClick = AccessibilityActionCompat(
                     AccessibilityNodeInfoCompat.ACTION_CLICK, "description"
                 )
                 info.addAction(customClick)
             }

            override fun onRequestSendAccessibilityEvent(host: ViewGroup?, child: View?, event: AccessibilityEvent): Boolean {
                logcat { event.toString() }
                return super.onRequestSendAccessibilityEvent(host, child, event)
            }
        })
    }

    private fun setupListAdapter() {
        binding.rvHomescreen.apply {
            adapter = listAdapter
            layoutManager = GridLayoutManager(requireContext(), 3)
            hasFixedSize()
        }
    }

    companion object {
        val icons = listOf(
            "Facebook",
            "Travel",
            "Instagram",
            "Trip",
            "Game",
            "Candy crush",
            "Football",
            "Basketball",
            "Hockey"
        )
    }
}