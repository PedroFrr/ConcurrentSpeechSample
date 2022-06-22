package com.example.multipleaudioplayer.scanning

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.setFragmentResult
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.example.multipleaudioplayer.R
import com.example.multipleaudioplayer.databinding.LayoutScanningExampleBinding
import com.example.multipleaudioplayer.scanning.nospatialization.ScanningNoSpatializationExampleFragment
import com.example.multipleaudioplayer.scanning.spatialization.ScanningSpatializationExampleFragment
import com.google.android.material.tabs.TabLayoutMediator
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding

class ScanningExampleFragment : Fragment(R.layout.layout_scanning_example) {

    private val binding by viewBinding(LayoutScanningExampleBinding::bind)

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
                0 -> "Espacialização"
                else -> "Sem espacialização"
            }
        }.attach()

        binding.pager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)

                when (position) {
                    0 -> {
                        setFragmentResult("requestKey", bundleOf("bundleKey" to "spatializationPageSelected"))
                    }
                    1 -> {
                        setFragmentResult("requestKey", bundleOf("bundleKey" to "noSpatializationPageSelected"))
                    }
                }
            }
        })
    }

    private inner class ConfigurationsSlidePageAdapter(fa: FragmentActivity) :
        FragmentStateAdapter(fa) {
        override fun getItemCount(): Int = 2

        override fun createFragment(position: Int): Fragment {
            val fragment = when (position) {
                0 -> ScanningSpatializationExampleFragment()
                else -> ScanningNoSpatializationExampleFragment()
            }
            return fragment
        }
    }
}