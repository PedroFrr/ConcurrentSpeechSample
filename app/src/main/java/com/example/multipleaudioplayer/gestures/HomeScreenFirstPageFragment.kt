package com.example.multipleaudioplayer.gestures

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.multipleaudioplayer.R
import com.example.multipleaudioplayer.databinding.LayoutHomescreenFirstPageBinding
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding


class HomeScreenFirstPageFragment : Fragment(R.layout.layout_homescreen_first_page) {

    private val binding by viewBinding(LayoutHomescreenFirstPageBinding::bind)

    private val listAdapter by lazy { ItemsListAdapter() }

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