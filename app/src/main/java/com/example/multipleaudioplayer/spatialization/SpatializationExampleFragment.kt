package com.example.multipleaudioplayer.spatialization

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.multipleaudioplayer.R
import com.example.multipleaudioplayer.databinding.LayoutSpatializationExampleBinding
import com.google.android.material.tabs.TabLayoutMediator
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding

class SpatializationExampleFragment : Fragment(R.layout.layout_spatialization_example) {

    private val binding by viewBinding(LayoutSpatializationExampleBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUi()
    }

    private fun setupUi() {
        // The pager adapter, which provides the pages to the view pager widget.
        val pagerAdapter = ConfigurationsSlidePageAdapter(requireActivity())
        binding.pager.adapter = pagerAdapter

        val tabLayout = binding.tabLayout
        TabLayoutMediator(tabLayout, binding.pager) { tab, position ->
            tab.text = when (position) {
                0 -> "Primeira notícia"
                else -> "Segunda notícia"
            }
        }.attach()
    }

    private inner class ConfigurationsSlidePageAdapter(fa: FragmentActivity) :
        FragmentStateAdapter(fa) {
        override fun getItemCount(): Int = 2

        override fun createFragment(position: Int): Fragment {
            val fragment = when (position) {
                0 -> SpatializationFirstNewsExampleFragment()
                else -> SpatializationSecondNewsExampleFragment()
            }
            return fragment
        }
    }
}