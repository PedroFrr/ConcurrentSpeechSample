package com.example.multipleaudioplayer.gestures

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.multipleaudioplayer.R
import com.example.multipleaudioplayer.databinding.LayoutGesturesExampleBinding
import com.example.multipleaudioplayer.databinding.LayoutHomescreenFirstPageBinding
import com.google.vr.sdk.audio.GvrAudioEngine
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers


class HomeScreenFirstPageFragment : Fragment(R.layout.layout_homescreen_first_page) {

    private val binding by viewBinding(LayoutHomescreenFirstPageBinding::bind)

    private val listAdapter by lazy { DailyAllowanceSummaryListAdapter() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUi()
    }

    private fun setupUi() {
        setupListAdapter()

        listAdapter.submitList(icons)
    }

    private fun setupListAdapter(){
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