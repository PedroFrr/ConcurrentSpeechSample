package com.example.multipleaudioplayer.audioproperties

import android.media.MediaPlayer
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.multipleaudioplayer.R
import com.example.multipleaudioplayer.databinding.LayoutAudioProprietiesSamplesExampleBinding
import com.google.vr.sdk.audio.GvrAudioEngine
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class AudioPropertiesSamplesExampleFragment :
    Fragment(R.layout.layout_audio_proprieties_samples_example) {

    private val binding by viewBinding(LayoutAudioProprietiesSamplesExampleBinding::bind)

    private val scope = CoroutineScope(Dispatchers.IO)

    private val mediaPlayerDocumentSampleCristiano: MediaPlayer by lazy {
        MediaPlayer.create(requireActivity(), R.raw.document_sample_cristiano)
    }

    private val mediaPlayerDocumentSampleInes: MediaPlayer by lazy {
        MediaPlayer.create(requireActivity(), R.raw.document_sample_ines)
    }

    private val mediaPlayerAudioProperties: MediaPlayer by lazy {
        MediaPlayer.create(requireActivity(), R.raw.audio_properties_sample)
    }

    private val mediaPlayerDocumentSampleWithPauses: MediaPlayer by lazy {
        MediaPlayer.create(requireActivity(), R.raw.audio_properties_with_pauses_male_voice)
    }

    private val mediaPlayerAudioPropertiesWithPauses: MediaPlayer by lazy {
        MediaPlayer.create(requireActivity(), R.raw.audio_properties_with_pauses)
    }

    private val mediaPlayerEarcon: MediaPlayer by lazy {
        MediaPlayer.create(requireActivity(), R.raw.earcon_with_pauses)
    }

    private val audioPropertiesSameTime: MediaPlayer by lazy {
        MediaPlayer.create(
            requireActivity(),
            R.raw.audio_properties_same_time_scenario
        )
    }

    private val audioPropertiesWithPausesFaster: MediaPlayer by lazy {
        MediaPlayer.create(
            requireActivity(),
            R.raw.pauses_with_earcon_at_end
        )
    }

    private val audioPropertiesWithPausesFasterAndEarcons: MediaPlayer by lazy {
        MediaPlayer.create(
            requireActivity(),
            R.raw.audio_properties_same_time_with_earcons
        )
    }

    private val audioPropertiesOnlyEarcons: MediaPlayer by lazy {
        MediaPlayer.create(requireActivity(), R.raw.audio_properties_only_with_earcons)
    }

    private val audioPropertiesOnlyVoiceSameTime: MediaPlayer by lazy {
        MediaPlayer.create(requireActivity(), R.raw.audio_properties_only_with_voice_same_time)
    }

    private val audioEngine by lazy {
        GvrAudioEngine(requireActivity(), GvrAudioEngine.RenderingMode.BINAURAL_HIGH_QUALITY)
    }

    private var mainDocumentSourceId = GvrAudioEngine.INVALID_ID
    private var audioPropertiesSourceId = GvrAudioEngine.INVALID_ID

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUi()
    }

    private fun setupUi() {
        binding.btnAudioPropertiesScenario.setOnClickListener {
            audioPropertiesOnlyVoiceSameTime.start()
        }

        binding.btnSpatialScenario.setOnClickListener {
            playSampleWithSpatialization()
        }

        binding.btnScenarioWithPausesFaster.setOnClickListener {
            audioPropertiesWithPausesFaster.start()
        }

        binding.btnScenarioWithPausesWithEarcon.setOnClickListener {
            audioPropertiesWithPausesFasterAndEarcons.start()
        }

        binding.btnScenarioOnlyWithEarcons.setOnClickListener {
            audioPropertiesOnlyEarcons.start()
        }

        setupMediaPlayerListeners()

        binding.cvMediaPlayerButtons.apply {
            btnStop.setOnClickListener {
                stopMediaPlayer(mediaPlayerDocumentSampleCristiano)
                stopMediaPlayer(mediaPlayerDocumentSampleInes)
                stopMediaPlayer(mediaPlayerAudioProperties)
                stopMediaPlayer(mediaPlayerDocumentSampleWithPauses)
                stopMediaPlayer(mediaPlayerAudioPropertiesWithPauses)
                stopMediaPlayer(mediaPlayerEarcon)
                stopMediaPlayer(audioPropertiesWithPausesFaster)
                stopMediaPlayer(audioPropertiesWithPausesFasterAndEarcons)
                stopMediaPlayer(audioPropertiesOnlyEarcons)
                stopMediaPlayer(audioPropertiesOnlyVoiceSameTime)
                stopMediaPlayer(audioPropertiesSameTime)

                if (audioEngine.isSoundPlaying(mainDocumentSourceId)) audioEngine.stopSound(mainDocumentSourceId)
                if (audioEngine.isSoundPlaying(audioPropertiesSourceId)) audioEngine.stopSound(audioPropertiesSourceId)

                toggleButtons(true)
            }
            btnPlay.setOnClickListener { }
            btnPause.setOnClickListener { }
        }
    }

    private fun playSampleWithSpatialization() {
        audioPropertiesSameTime.start()
    }

    private fun setupMediaPlayerListeners() {
        mediaPlayerDocumentSampleCristiano.setOnCompletionListener {
            binding.btnAudioPropertiesScenario.isEnabled = true
        }

        mediaPlayerDocumentSampleInes.setOnCompletionListener {
            binding.btnAudioPropertiesScenario.isEnabled = true
        }
    }

    private fun toggleButtons(isEnabled: Boolean) {
        binding.apply {
            btnAudioPropertiesScenario.isEnabled = isEnabled
            btnScenarioWithPauses.isEnabled = isEnabled
            btnSpatialScenario.isEnabled = isEnabled
            btnScenarioWithPausesWithEarcon.isEnabled = isEnabled
            btnScenarioOnlyWithEarcons.isEnabled = isEnabled
            btnScenarioWithPausesFaster.isEnabled = isEnabled
        }
    }

    private fun stopMediaPlayer(mediaPlayer: MediaPlayer) {
        if (mediaPlayer.isPlaying) {
            mediaPlayer.pause()
            mediaPlayer.seekTo(0)
        }
    }

    override fun onDestroyView() {
        mediaPlayerDocumentSampleCristiano.stop()
        mediaPlayerDocumentSampleInes.stop()
        mediaPlayerAudioProperties.stop()
        mediaPlayerAudioPropertiesWithPauses.stop()
        mediaPlayerDocumentSampleWithPauses.stop()
        audioPropertiesSameTime.stop()

        audioEngine.pause()

        super.onDestroyView()
    }
}