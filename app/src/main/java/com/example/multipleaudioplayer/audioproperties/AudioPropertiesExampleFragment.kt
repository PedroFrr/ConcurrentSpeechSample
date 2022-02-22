package com.example.multipleaudioplayer.audioproperties

import android.media.MediaPlayer
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.multipleaudioplayer.R
import com.example.multipleaudioplayer.databinding.LayoutAudioPropertiesExampleBinding
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

class AudioPropertiesExampleFragment : Fragment(R.layout.layout_audio_properties_example) {

    private val binding by viewBinding(LayoutAudioPropertiesExampleBinding::bind)

    private val scope = CoroutineScope(Dispatchers.IO)

    private val mediaPlayer: MediaPlayer by lazy {
        MediaPlayer.create(requireActivity(), R.raw.document_with_properties)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUi()

        playMockAudioPropertiesSample()

    }

    private fun setupUi() {

    }

    private fun playMockAudioPropertiesSample() {
        mediaPlayer.start()
    }

    override fun onPause() {
        mediaPlayer.stop()
        super.onPause()
    }

    companion object {
        private const val sampleText = "Este é um exemplo de um texto que vai ser dividido"
        private const val TAG = "Text to Speech"
    }
}