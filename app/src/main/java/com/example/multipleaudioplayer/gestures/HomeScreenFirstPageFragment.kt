package com.example.multipleaudioplayer.gestures

import android.annotation.SuppressLint
import android.content.Context
import android.media.MediaPlayer
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
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