package com.example.multipleaudioplayer.gestures

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.example.multipleaudioplayer.R
import com.example.multipleaudioplayer.databinding.LayoutGesturesExampleBinding
import com.example.multipleaudioplayer.utils.showToast
import com.google.android.material.tabs.TabLayoutMediator
import com.google.vr.sdk.audio.GvrAudioEngine
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val NUM_PAGES = 2

class GesturesExampleFragment : Fragment(R.layout.layout_gestures_example) {

    private val binding by viewBinding(LayoutGesturesExampleBinding::bind)

    private val scope = CoroutineScope(Dispatchers.IO)

    private lateinit var viewPager: ViewPager2

    private val audioEngine by lazy {
        GvrAudioEngine(requireActivity(), GvrAudioEngine.RenderingMode.BINAURAL_HIGH_QUALITY)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUi()

    }

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

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                showToast(position.toString())
                super.onPageSelected(position)
            }
        })

        binding.srlHomescreen.setOnRefreshListener {
            showToast("Updating............")

            scope.launch {
                delay(2000)
                withContext(Dispatchers.Main) {
                    binding.srlHomescreen.isRefreshing = false
                }
            }
        }
    }

    companion object {
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