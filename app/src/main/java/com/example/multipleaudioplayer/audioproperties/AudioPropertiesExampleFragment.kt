package com.example.multipleaudioplayer.audioproperties

import android.media.MediaPlayer
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import com.example.multipleaudioplayer.R
import com.example.multipleaudioplayer.databinding.LayoutAudioPropertiesExampleBinding
import com.google.vr.sdk.audio.GvrAudioEngine
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class AudioPropertiesExampleFragment : Fragment(R.layout.layout_audio_properties_example) {

    private val binding by viewBinding(LayoutAudioPropertiesExampleBinding::bind)

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
        val voices = resources.getStringArray(R.array.Voices)
        val adapter = ArrayAdapter(requireContext(), R.layout.support_simple_spinner_dropdown_item, voices)
        binding.spinnerVoices.adapter = adapter

        binding.btnAudioPropertiesScenario.setOnClickListener {
            playMockAudioPropertiesSample()
        }

        binding.btnSpatialScenario.setOnClickListener {
            playSampleWithSpatialization()
        }

        binding.btnScenarioWithPauses.setOnClickListener {
            playAudioPropertiesScenarioWithPauses()
        }

        binding.btnScenarioWithPausesWithEarcon.setOnClickListener {
            playAudioPropertiesWithEarcons()
        }

        setupMediaPlayerListeners()
    }

    private fun playAudioPropertiesScenarioWithPauses() {
        scope.launch {
            awaitAll(async {
                playDocumentSampleWithPauses()
            }, async {
                playAudioPropertiesWithPauses()
            })
        }
    }

    private fun playAudioPropertiesWithEarcons() {
        scope.launch {
            awaitAll(async {
                playDocumentSampleWithPauses()
            }, async {
                playAudioPropertiesWithPauses()
            },
            async {
                playEarcons()
            })
        }
    }

    private fun playMockAudioPropertiesSample() {
        binding.btnAudioPropertiesScenario.isEnabled = false
        scope.launch {
            awaitAll(async {
                playDocumentSample()
            }, async {
                playAudioProperties()
            })
        }
    }

/*
    private fun playMockAudioPropertiesSample() {
*//*        val params = PlaybackParams()
        params.pitch = 0.75f

        mediaPlayerDocumentSampleCristiano.playbackParams = params*//*

        playDocumentSample()

    }*/

    private fun playSampleWithSpatialization() {
        binding.btnSpatialScenario.isEnabled = false
        binding.btnAudioPropertiesScenario.isEnabled = false
        scope.launch {
            audioEngine.preloadSoundFile(MALE_VOICE_MAIN_DOCUMENT)
            audioEngine.preloadSoundFile(MALE_VOICE_AUDIO_PROPERTIES)

            mainDocumentSourceId = audioEngine.createSoundObject(MALE_VOICE_MAIN_DOCUMENT)
            audioPropertiesSourceId = audioEngine.createSoundObject(MALE_VOICE_AUDIO_PROPERTIES)

            audioEngine.setSoundObjectPosition(mainDocumentSourceId, -8.0f, 0.0f, 0.0f)
            audioEngine.setSoundObjectPosition(audioPropertiesSourceId, 8.0f, 0.0f, 0.0f)

            audioEngine.playSound(mainDocumentSourceId, false)
            audioEngine.playSound(audioPropertiesSourceId, false)

            withContext(Dispatchers.Main) {
                binding.btnSpatialScenario.isEnabled = true
                binding.btnAudioPropertiesScenario.isEnabled = true
            }
        }
    }

    private fun playDocumentSample() {
        when (binding.spinnerVoices.selectedItem.toString()) {
            "Cristiano" -> mediaPlayerDocumentSampleCristiano.start()
            "Ines" -> mediaPlayerDocumentSampleInes.start()
        }
    }

    private fun playAudioProperties() {
        mediaPlayerAudioProperties.start()
    }

    private fun playDocumentSampleWithPauses() {
        mediaPlayerDocumentSampleWithPauses.start()
    }

    private fun playAudioPropertiesWithPauses() {
        mediaPlayerAudioPropertiesWithPauses.start()
    }

    private fun playEarcons() {
        mediaPlayerEarcon.start()
    }

    private fun setupMediaPlayerListeners() {
        mediaPlayerDocumentSampleCristiano.setOnCompletionListener {
            binding.btnAudioPropertiesScenario.isEnabled = true
        }

        mediaPlayerDocumentSampleInes.setOnCompletionListener {
            binding.btnAudioPropertiesScenario.isEnabled = true
        }
    }

    override fun onPause() {
        mediaPlayerDocumentSampleCristiano.stop()
        mediaPlayerDocumentSampleInes.stop()
        mediaPlayerAudioProperties.stop()
        mediaPlayerAudioPropertiesWithPauses.stop()
        mediaPlayerDocumentSampleWithPauses.stop()

        audioEngine.pause()
        super.onPause()
    }

    companion object {
        private const val MALE_VOICE_MAIN_DOCUMENT = "audio_properties_main_document.mp3"
        private const val MALE_VOICE_AUDIO_PROPERTIES = "audio_properties_male_voice.mp3"
    }
}