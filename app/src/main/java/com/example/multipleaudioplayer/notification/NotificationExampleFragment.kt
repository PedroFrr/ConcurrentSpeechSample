package com.example.multipleaudioplayer.notification

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.multipleaudioplayer.R
import com.example.multipleaudioplayer.databinding.LayoutNotificationExampleBinding
import com.example.multipleaudioplayer.notification.nospatialization.NotificationNoSpatializationExampleFragment
import com.example.multipleaudioplayer.notification.spatialization.NotificationSpatializationExampleFragment
import com.google.android.material.tabs.TabLayoutMediator
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding

class NotificationExampleFragment : Fragment(R.layout.layout_notification_example) {

    private val binding by viewBinding(LayoutNotificationExampleBinding::bind)

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
                else -> "Sem Espacialização"
            }
        }.attach()
    }

    private inner class ConfigurationsSlidePageAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {
        override fun getItemCount(): Int = 2

        override fun createFragment(position: Int): Fragment {
            val fragment = when (position) {
                0 -> NotificationSpatializationExampleFragment()
                else -> NotificationNoSpatializationExampleFragment()
            }
            return fragment
        }
    }

}