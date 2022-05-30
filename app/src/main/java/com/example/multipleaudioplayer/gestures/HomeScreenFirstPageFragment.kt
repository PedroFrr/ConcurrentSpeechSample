package com.example.multipleaudioplayer.gestures

import android.annotation.SuppressLint
import android.content.Context
import android.media.MediaPlayer
import android.os.Bundle
import android.view.View
import android.view.accessibility.AccessibilityManager
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.multipleaudioplayer.R
import com.example.multipleaudioplayer.databinding.LayoutHomescreenFirstPageBinding
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding


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
        MediaPlayer.create(context, R.raw.switch_on)
    }

    private val radioButtonMediaPlayer: MediaPlayer by lazy {
        MediaPlayer.create(context, R.raw.radio_button_on)
    }

    private val checkboxMediaPlayer: MediaPlayer by lazy {
        MediaPlayer.create(context, R.raw.check_box)
    }

    private val isTalkbackEnabled: Boolean by lazy {
        val am = binding.root.context.getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager
        am.isTouchExplorationEnabled
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
                if (!isTalkbackEnabled) checkboxMediaPlayer.start()
            }
            et.setOnClickListener {
                if (!isTalkbackEnabled) editTextMediaPlayer.start()
            }
            iv.setOnClickListener {
                if (!isTalkbackEnabled) imageViewMediaPlayer.start()
            }
            rbOption1.setOnClickListener {
                if (!isTalkbackEnabled) radioButtonMediaPlayer.start()
            }
            rbOption2.setOnClickListener {
                if (!isTalkbackEnabled) radioButtonMediaPlayer.start()
            }
            sv.setOnClickListener {
                if (!isTalkbackEnabled) switchMediaPlayer.start()
            }
            tv.setOnClickListener {
                if (!isTalkbackEnabled) textViewMediaPlayer.start()
            }
        }
    }

    private fun setupListAdapter() {
        val myGridLinearLayoutManager = object : GridLayoutManager(requireContext(), 3) {
            override fun canScrollVertically(): Boolean {
                return false
            }
        }
        binding.rvHomescreen.apply {
            adapter = listAdapter
            layoutManager = myGridLinearLayoutManager
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