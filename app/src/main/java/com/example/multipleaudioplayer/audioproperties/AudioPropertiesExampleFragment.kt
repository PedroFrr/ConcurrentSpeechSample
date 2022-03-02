package com.example.multipleaudioplayer.audioproperties

import android.media.MediaPlayer
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import com.example.multipleaudioplayer.R
import com.example.multipleaudioplayer.databinding.LayoutAudioPropertiesExampleBinding
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch


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

        setupMediaPlayerListeners()
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

    private fun playDocumentSample() {
        when(binding.spinnerVoices.selectedItem.toString()){
            "Cristiano" -> mediaPlayerDocumentSampleCristiano.start()
            "Ines" -> mediaPlayerDocumentSampleInes.start()
        }
    }

    private fun playAudioProperties() {
        mediaPlayerAudioProperties.start()
    }

    private fun setupMediaPlayerListeners(){
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
        super.onPause()
    }

    companion object {
        private const val sampleText = "Este é um exemplo de um texto que vai ser dividido"
        private const val TAG = "Text to Speech"
    }
}