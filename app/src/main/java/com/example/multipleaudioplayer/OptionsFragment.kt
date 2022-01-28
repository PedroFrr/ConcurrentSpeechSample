package com.example.multipleaudioplayer

import android.os.Bundle
import android.view.View
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.multipleaudioplayer.databinding.LayoutSampleOptionsFragmentBinding
import com.example.multipleaudioplayer.utils.viewBinding

class OptionsFragment : Fragment(R.layout.layout_sample_options_fragment) {

    private val binding by viewBinding(LayoutSampleOptionsFragmentBinding::bind)


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUi()
    }

    private fun setupUi() {
        binding.btnNotificationScenario.setOnClickListener {
            navigateTo(R.id.options_to_notification_example)
        }

        binding.btnScanningScenario.setOnClickListener {
            navigateTo(R.id.options_to_notification_example)
        }
    }

    private fun navigateTo(@IdRes navigationId: Int) {
        findNavController().navigate(navigationId)
    }
}