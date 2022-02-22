package com.example.multipleaudioplayer.scanning

import android.content.ContextWrapper
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.view.View
import androidx.fragment.app.Fragment
import androidx.work.OneTimeWorkRequest
import com.example.multipleaudioplayer.R
import com.example.multipleaudioplayer.databinding.LayoutScanningExampleBinding
import com.example.multipleaudioplayer.workers.DeleteAudioFilesWorker
import com.example.multipleaudioplayer.workers.MixAudioWorker
import com.example.multipleaudioplayer.workers.SynthesizeFileWorker
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding

class ScanningExampleFragment : Fragment(R.layout.layout_scanning_example) {

    private val binding by viewBinding(LayoutScanningExampleBinding::bind)

    private val oneDocumentSourceMediaPlayer: MediaPlayer by lazy {
        MediaPlayer.create(
            requireActivity(),
            R.raw.one_source_document
        )
    }

    private val twoDocumentSourcePart1MediaPlayer: MediaPlayer by lazy {
        MediaPlayer.create(
            requireActivity(),
            R.raw.two_source_document_part1
        )
    }

    private val twoDocumentSourcePart2MediaPlayer: MediaPlayer by lazy {
        MediaPlayer.create(
            requireActivity(),
            R.raw.two_source_document_part2
        )
    }

    private val threeDocumentSourcePart1MediaPlayer: MediaPlayer by lazy {
        MediaPlayer.create(
            requireActivity(),
            R.raw.three_source_document_part1
        )
    }

    private val threeDocumentSourcePart2MediaPlayer: MediaPlayer by lazy {
        MediaPlayer.create(
            requireActivity(),
            R.raw.three_source_document_part2
        )
    }

    private val threeDocumentSourcePart3MediaPlayer: MediaPlayer by lazy {
        MediaPlayer.create(
            requireActivity(),
            R.raw.three_source_document_part3
        )
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


        val synthesizeFileWorker = OneTimeWorkRequest.from(SynthesizeFileWorker::class.java)
        val mixAudioWorker = OneTimeWorkRequest.from(MixAudioWorker::class.java)
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
        binding.btnPlayScenario.setOnClickListener {

            when (binding.sliderVoicesConfiguration.value) {
                1f -> {
                    stopOneDocumentSource()
                    stopTwoDocumentSource()
                    stopThreeDocumentSource()
                    playOneDocumentSource()
                }
                2f -> {
                    stopOneDocumentSource()
                    stopTwoDocumentSource()
                    stopThreeDocumentSource()
                    playTwoDocumentSource()
                }
                3f -> {
/*                    stopOneDocumentSource()
                    stopTwoDocumentSource()
                    stopThreeDocumentSource()*/
                    playThreeDocumentSource()
                }
            }
        }
    }

    private fun playOneDocumentSource() {
        oneDocumentSourceMediaPlayer.seekTo(0)
        oneDocumentSourceMediaPlayer.start()
    }

    private fun stopOneDocumentSource() {
        if(oneDocumentSourceMediaPlayer.isPlaying){
            oneDocumentSourceMediaPlayer.pause()
        }
    }

    private fun playTwoDocumentSource() {
        twoDocumentSourcePart1MediaPlayer.seekTo(0)
        twoDocumentSourcePart1MediaPlayer.start()
        twoDocumentSourcePart2MediaPlayer.seekTo(0)
        twoDocumentSourcePart2MediaPlayer.start()
    }

    private fun stopTwoDocumentSource() {
        if(twoDocumentSourcePart1MediaPlayer.isPlaying){
            twoDocumentSourcePart1MediaPlayer.pause()
            twoDocumentSourcePart2MediaPlayer.pause()
        }
    }

    private fun playThreeDocumentSource() {
        threeDocumentSourcePart1MediaPlayer.seekTo(0)
        threeDocumentSourcePart1MediaPlayer.start()
        threeDocumentSourcePart2MediaPlayer.seekTo(0)
        threeDocumentSourcePart2MediaPlayer.start()
        threeDocumentSourcePart3MediaPlayer.seekTo(0)
        threeDocumentSourcePart3MediaPlayer.start()
    }

    private fun stopThreeDocumentSource() {
        if (threeDocumentSourcePart1MediaPlayer.isPlaying){
            threeDocumentSourcePart1MediaPlayer.pause()
            threeDocumentSourcePart2MediaPlayer.pause()
            threeDocumentSourcePart3MediaPlayer.pause()
        }
    }

    override fun onPause() {
        oneDocumentSourceMediaPlayer.stop()
        twoDocumentSourcePart1MediaPlayer.stop()
        twoDocumentSourcePart2MediaPlayer.stop()
        threeDocumentSourcePart1MediaPlayer.stop()
        threeDocumentSourcePart2MediaPlayer.stop()
        threeDocumentSourcePart3MediaPlayer.stop()
        super.onPause()
    }

    companion object {
        private const val sampleText = "Este é um exemplo de um texto que vai ser dividido"
        private const val TAG = "Text to Speech"
    }
}