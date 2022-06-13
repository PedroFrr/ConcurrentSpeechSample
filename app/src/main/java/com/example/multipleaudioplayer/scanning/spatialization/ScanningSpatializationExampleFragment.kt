package com.example.multipleaudioplayer.scanning.spatialization

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.multipleaudioplayer.R
import com.example.multipleaudioplayer.databinding.LayoutScanningSpatializationBinding
import com.example.multipleaudioplayer.utils.getResourceUri
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.util.MimeTypes
import com.google.android.exoplayer2.util.Util
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding

class ScanningSpatializationExampleFragment : Fragment(R.layout.layout_scanning_spatialization) {

    private val binding by viewBinding(LayoutScanningSpatializationBinding::bind)

    private var numberOfTimesOneVoiceButtonWasClicked = -1
    private var numberOfTimesTwoVoiceButtonWasClicked = -1
    private var numberOfTimesThreeVoiceButtonWasClicked = -1
    private var numberOfTimesFourVoiceButtonWasClicked = -1

    private var player: ExoPlayer? = null
    private var playWhenReady = true
    private var currentItem = 0
    private var startPosition = 0L

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUi()
    }

    private fun setupUi() {
        binding.btnSpatialScenarioTwoVoices.setOnClickListener {
            initializePlayer(2)
        }

        binding.btnSpatialScenarioThreeVoices.setOnClickListener {
            initializePlayer(3)
        }

        binding.btnSpatialScenarioFourVoices.setOnClickListener {
            initializePlayer(4)
        }

        binding.cvMediaPlayerButtons.apply {
            btnStop.setOnClickListener {
                toggleButtons(isEnable = true)

                releasePlayer()
            }
            btnPlay.setOnClickListener { }
            btnPause.setOnClickListener { }
        }
    }

    private fun toggleButtons(isEnable: Boolean) {
        binding.apply {
            btnSpatialScenarioOneVoice.isEnabled = isEnable
            btnSpatialScenarioTwoVoices.isEnabled = isEnable
            btnSpatialScenarioThreeVoices.isEnabled = isEnable
            btnSpatialScenarioFourVoices.isEnabled = isEnable
            cvMediaPlayerButtons.btnStop.isEnabled = !isEnable // this one is the opposite. When scenario buttons are enabled, the play button is disabled
        }
    }

    private fun initializePlayer(numberOfVoices: Int) {
        releasePlayer()
        toggleButtons(isEnable = false)

        when (numberOfVoices) {
            1 -> {
                if (numberOfTimesOneVoiceButtonWasClicked < 1) numberOfTimesOneVoiceButtonWasClicked++ else numberOfTimesOneVoiceButtonWasClicked = 0
            }
            2 -> {
                if (numberOfTimesTwoVoiceButtonWasClicked < 1) numberOfTimesTwoVoiceButtonWasClicked++ else numberOfTimesTwoVoiceButtonWasClicked = 0
            }
            3 -> {
                if (numberOfTimesThreeVoiceButtonWasClicked < 1) numberOfTimesThreeVoiceButtonWasClicked++ else numberOfTimesThreeVoiceButtonWasClicked = 0
            }
            else -> {
                if (numberOfTimesFourVoiceButtonWasClicked < 1) numberOfTimesFourVoiceButtonWasClicked++ else numberOfTimesFourVoiceButtonWasClicked = 0
            }
        }

        val audioResourceId = when (numberOfVoices) {
            2 -> {
                if (numberOfTimesTwoVoiceButtonWasClicked == 0) R.raw.two_voices_first_news else R.raw.two_voices_second_news
            }
            3 -> {
                if (numberOfTimesThreeVoiceButtonWasClicked == 0) R.raw.three_voices_first_news else R.raw.three_voices_second_news
            }
            4 -> {
                if (numberOfTimesFourVoiceButtonWasClicked == 0) R.raw.four_voices_first_news else R.raw.four_voices_second_news
            }
            else -> {
                R.raw.two_voices_first_news
            }
        }

        player = ExoPlayer.Builder(requireContext())
            .build()
            .also { exoPlayer ->
                val uri = requireContext().getResourceUri(audioResourceId)
                val mediaItem = MediaItem.Builder()
                    .setUri(uri)
                    .setMimeType(MimeTypes.AUDIO_WAV)
                    .build()

                exoPlayer.setMediaItem(mediaItem)
                //exoPlayer.playWhenReady = playWhenReady
                exoPlayer.seekTo(currentItem, startPosition)

                exoPlayer.prepare()

                exoPlayer.play()
            }

    }

    override fun onPause() {
        super.onPause()

        if (Util.SDK_INT <= 23) {
            releasePlayer()
        }
    }

    override fun onStop() {
        super.onStop()
        if (Util.SDK_INT > 23) {
            releasePlayer()
        }
    }

    private fun releasePlayer() {
        player?.let { exoPlayer ->
            currentItem = exoPlayer.currentMediaItemIndex
            playWhenReady = exoPlayer.playWhenReady
            exoPlayer.release()
        }
        player = null
    }
}
