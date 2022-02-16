package com.example.multipleaudioplayer.audioproperties

import android.content.ContextWrapper
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.multipleaudioplayer.R
import com.example.multipleaudioplayer.databinding.LayoutAudioPropertiesExampleBinding
import com.example.multipleaudioplayer.databinding.LayoutScanningExampleBinding
import com.example.multipleaudioplayer.workers.DeleteAudioFilesWorker
import com.example.multipleaudioplayer.workers.MixAudioWorker
import com.example.multipleaudioplayer.workers.MixAudioWorker.Companion.Progress
import com.example.multipleaudioplayer.workers.SynthesizeFileWorker
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.util.*

class AudioPropertiesExampleFragment : Fragment(R.layout.layout_audio_properties_example) {

    private val binding by viewBinding(LayoutAudioPropertiesExampleBinding::bind)

    private val scope = CoroutineScope(Dispatchers.IO)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUi()

    }

    private fun setupUi() {

    }

    companion object {
        private const val sampleText = "Este é um exemplo de um texto que vai ser dividido"
        private const val TAG = "Text to Speech"
    }
}