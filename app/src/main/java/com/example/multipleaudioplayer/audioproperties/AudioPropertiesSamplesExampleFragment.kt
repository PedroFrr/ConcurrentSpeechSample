package com.example.multipleaudioplayer.audioproperties

import android.media.MediaPlayer
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import com.example.multipleaudioplayer.R
import com.example.multipleaudioplayer.databinding.LayoutAudioProprietiesSamplesExampleBinding
import com.google.vr.sdk.audio.GvrAudioEngine
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding
import kotlinx.coroutines.*


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

    private val audioPropertiesWithPauses: MediaPlayer by lazy {
        MediaPlayer.create(
            requireActivity(),
            R.raw.audio_properties_main_with_cristiano_and_pauses_with_ines
        )
    }

    private val audioPropertiesWithPausesFaster: MediaPlayer by lazy {
        MediaPlayer.create(
            requireActivity(),
            R.raw.audio_properties_main_with_cristiano_and_pauses_with_ines_faster
        )
    }

    private val audioPropertiesWithPausesFasterAndEarcons: MediaPlayer by lazy {
        MediaPlayer.create(
            requireActivity(),
            R.raw.audio_properties_main_with_cristiano_and_pauses_with_ines_faster_and_earcons
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

        scope.launch {
            withContext(Dispatchers.Main) {
                toggleButtons(isEnabled = false)
6            }
            audioEngine.preloadSoundFile(MALE_VOICE_MAIN_DOCUMENT_NEW)
            audioEngine.preloadSoundFile(MALE_VOICE_AUDIO_PROPERTIES_NEW)

            withContext(Dispatchers.Main) {
                toggleButtons(isEnabled = true)
            }
        }

        setupUi()
    }

    private fun setupUi() {
        val voices = resources.getStringArray(R.array.Voices)
        val adapter =
            ArrayAdapter(requireContext(), R.layout.support_simple_spinner_dropdown_item, voices)
        binding.spinnerVoices.adapter = adapter

        binding.btnAudioPropertiesScenario.setOnClickListener {
            audioPropertiesOnlyVoiceSameTime.start()
        }

        binding.btnSpatialScenario.setOnClickListener {
            playSampleWithSpatialization()
        }

        binding.btnScenarioWithPauses.setOnClickListener {
            audioPropertiesWithPauses.start()
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
        scope.launch {
            withContext(Dispatchers.Main) {
                toggleButtons(isEnabled = false)
            }
            awaitAll(async {
                playDocumentSample()
            }, async {
                playAudioProperties()
            })

            withContext(Dispatchers.Main) {
                toggleButtons(isEnabled = true)
            }
        }
    }

    private fun playSampleWithSpatialization() {
        scope.launch {
            withContext(Dispatchers.Main) {
                toggleButtons(isEnabled = false)
            }

            mainDocumentSourceId = audioEngine.createSoundObject(MALE_VOICE_MAIN_DOCUMENT_NEW)
            audioPropertiesSourceId = audioEngine.createSoundObject(MALE_VOICE_AUDIO_PROPERTIES_NEW)

            audioEngine.setSoundObjectPosition(mainDocumentSourceId, -8.0f, 0.0f, 0.0f)
            audioEngine.setSoundObjectPosition(audioPropertiesSourceId, 8.0f, 0.0f, 0.0f)


            awaitAll(async {
                audioEngine.playSound(mainDocumentSourceId, false)
            }, async {
                audioEngine.playSound(audioPropertiesSourceId, false)
            })

            withContext(Dispatchers.Main) {
                toggleButtons(isEnabled = true)
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
        private const val MALE_VOICE_MAIN_DOCUMENT_NEW =
            "document_audio_properties_only_with_voice_same_time.mp3"
        private const val MALE_VOICE_AUDIO_PROPERTIES_NEW =
            "properties_audio_properties_only_with_voice_same_time.mp3"
    }
}