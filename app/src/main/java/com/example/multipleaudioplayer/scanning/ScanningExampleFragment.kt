package com.example.multipleaudioplayer.scanning

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

class ScanningExampleFragment : Fragment(R.layout.layout_scanning_example) {

    private val binding by viewBinding(LayoutScanningExampleBinding::bind)

    private val mediaPlayer1: MediaPlayer by lazy {
        MediaPlayer.create(requireActivity(), R.raw.part1)
    }

    private val mediaPlayer2: MediaPlayer by lazy {
        MediaPlayer.create(requireActivity(), R.raw.part2)
    }

    private val mediaPlayer3: MediaPlayer by lazy {
        MediaPlayer.create(requireActivity(), R.raw.part3)
    }

    private val maxSpeechInputLength
        get() = TextToSpeech.getMaxSpeechInputLength()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUi()

        /*sampleText
            .chunked(3)
            .forEach { text ->
                TextT
            }*/

        mediaPlayer1.start()
        mediaPlayer2.start()
        mediaPlayer3.start()


        val synthesizeFileWorker = OneTimeWorkRequest.from(SynthesizeFileWorker::class.java)
        val mixAudioWorker =  OneTimeWorkRequest.from(MixAudioWorker::class.java)
        val deleteFilesWorker = OneTimeWorkRequest.from(DeleteAudioFilesWorker::class.java)

/*       WorkManager.getInstance(requireContext())
            .beginWith(mixAudioWorker)
            .enqueue()*/

        val wrapper = ContextWrapper(requireContext())
        val directory = wrapper.filesDir

        val outputPath = "$directory/output_file.mp3"

        val mediaPlayer = MediaPlayer.create(requireContext(), Uri.parse(outputPath))
//        mediaPlayer.start()

    }

    private fun setupUi() {

    }

    companion object {
        private const val sampleText = "Este é um exemplo de um texto que vai ser dividido"
        private const val TAG = "Text to Speech"
    }
}