package com.example.multipleaudioplayer.audioproperties

import android.media.MediaPlayer
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.multipleaudioplayer.R
import com.example.multipleaudioplayer.databinding.LayoutAudioPropertiesEarconsExampleBinding
import com.example.multipleaudioplayer.databinding.LayoutAudioPropertiesExampleBinding
import com.example.multipleaudioplayer.notification.nospatialization.NotificationNoSpatializationExampleFragment
import com.example.multipleaudioplayer.notification.spatialization.NotificationSpatializationExampleFragment
import com.google.android.material.tabs.TabLayoutMediator
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding


class AudioPropertiesEarconsExampleFragment : Fragment(R.layout.layout_audio_properties_earcons_example) {

    private val binding by viewBinding(LayoutAudioPropertiesEarconsExampleBinding::bind)

    private val hiperligacaoMediaPlayer: MediaPlayer by lazy {
        MediaPlayer.create(requireActivity(), R.raw.hiperligacao_earcon)
    }

    private val negritoMediaPlayer: MediaPlayer by lazy {
        MediaPlayer.create(requireActivity(), R.raw.negrito_earcon)
    }

    private val italicoMediaPlayer: MediaPlayer by lazy {
        MediaPlayer.create(requireActivity(), R.raw.italico_earcon)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUi()
    }

    private fun setupUi() {
        binding.apply {
            btnHiperligacao.setOnClickListener {
                hiperligacaoMediaPlayer.start()
            }

            btnItalico.setOnClickListener {
                italicoMediaPlayer.start()
            }

            btnNegrito.setOnClickListener {
                negritoMediaPlayer.start()
            }
        }
    }
}