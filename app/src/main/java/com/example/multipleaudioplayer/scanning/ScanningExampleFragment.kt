package com.example.multipleaudioplayer.scanning

import android.content.ContextWrapper
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.amazonaws.auth.CognitoCachingCredentialsProvider
import com.amazonaws.regions.Regions
import com.amazonaws.services.polly.AmazonPollyPresigningClient
import com.amazonaws.services.polly.model.OutputFormat
import com.amazonaws.services.polly.model.SynthesizeSpeechPresignRequest
import com.amazonaws.services.polly.model.TextType
import com.example.multipleaudioplayer.R
import com.example.multipleaudioplayer.databinding.LayoutScanningExampleBinding
import com.example.multipleaudioplayer.utils.convertToSsml
import com.example.multipleaudioplayer.workers.DeleteAudioFilesWorker
import com.example.multipleaudioplayer.workers.MixAudioWorker
import com.example.multipleaudioplayer.workers.SynthesizeFileWorker
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.net.URL

class ScanningExampleFragment : Fragment(R.layout.layout_scanning_example) {

    private val binding by viewBinding(LayoutScanningExampleBinding::bind)

    private val scope = CoroutineScope(Dispatchers.IO)

    var mediaPlayer1: MediaPlayer? = null

    var mediaPlayer2: MediaPlayer? = null

    var mediaPlayer3: MediaPlayer? = null

    var mediaPlayer4: MediaPlayer? = null


    var client: AmazonPollyPresigningClient? = null

    private val maxSpeechInputLength
        get() = TextToSpeech.getMaxSpeechInputLength()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initPollyClient()

        setupUi()

        setupFirstNewMediaPlayer()
        setupSecondNewMediaPlayer()
        setupThirdNewMediaPlayer()
        setupFourthNewMediaPlayer()

        /*sampleText
            .chunked(3)
            .forEach { text ->
                TextT
            }*/


        val synthesizeFileWorker = OneTimeWorkRequest.from(SynthesizeFileWorker::class.java)
        val mixAudioWorker = OneTimeWorkRequest.from(MixAudioWorker::class.java)
        val deleteFilesWorker = OneTimeWorkRequest.from(DeleteAudioFilesWorker::class.java)

       WorkManager.getInstance(requireContext())
            .beginWith(mixAudioWorker)
            .enqueue()

        val wrapper = ContextWrapper(requireContext())
        val directory = wrapper.filesDir

        val outputPath = "$directory/output_file.mp3"

        val mediaPlayer = MediaPlayer.create(requireContext(), Uri.parse(outputPath))
//        mediaPlayer.start()

    }

    private fun setupUi() {
//        initAdapter()

        setupSpinner()

        setupVoiceSpinners()

        binding.btnPlayScenario.setOnClickListener {
/*            when (binding.sliderVoicesConfiguration.value) {
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
                    stopOneDocumentSource()
                    stopTwoDocumentSource()
                    stopThreeDocumentSource()
                    playThreeDocumentSource()
                }
            }*/

            playVoice()
        }
    }

    private fun initPollyClient() {
        val credentialsProvider = CognitoCachingCredentialsProvider(
            requireContext(),
            "ap-northeast-2:c903f1b4-7a90-42ff-a53a-df75f34036b2",
            Regions.AP_NORTHEAST_2
        )
        client = AmazonPollyPresigningClient(credentialsProvider)
    }

    private fun setupSpinner() {
        // setup spinner (with portuguese voices)
        val listOfAudioChannels = listOf(1, 2, 3, 4)
        val adapter = ArrayAdapter(
            requireContext(),
            R.layout.support_simple_spinner_dropdown_item,
            listOfAudioChannels
        )
        binding.spinnerVoiceSelection.adapter = adapter

        binding.spinnerVoiceSelection.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View,
                    position: Int,
                    id: Long
                ) {
                    setAudioChannelPropertiesVisibility(listOfAudioChannels[position])
                }

                override fun onNothingSelected(parent: AdapterView<*>) {

                }
            }
    }

    private fun setupVoiceSpinners() {
        val voices = resources.getStringArray(R.array.Voices)
        val adapter =
            ArrayAdapter(requireContext(), R.layout.support_simple_spinner_dropdown_item, voices)
        binding.firstAudioChannelProperties.spinnerVoices.adapter = adapter
        binding.secondAudioChannelProperties.spinnerVoices.adapter = adapter
        binding.thirdAudioChannelProperties.spinnerVoices.adapter = adapter
        binding.fourthAudioChannelProperties.spinnerVoices.adapter = adapter
    }

    private fun setAudioChannelPropertiesVisibility(selectedAudioChannel: Int = 1) {
        binding.firstAudioChannelProperties.audioChannelProperties.isVisible =
            selectedAudioChannel == 1
        binding.secondAudioChannelProperties.audioChannelProperties.isVisible =
            selectedAudioChannel == 2
        binding.thirdAudioChannelProperties.audioChannelProperties.isVisible =
            selectedAudioChannel == 3
        binding.fourthAudioChannelProperties.audioChannelProperties.isVisible =
            selectedAudioChannel == 4

    }

    private fun playVoice() {

        binding.btnPlayScenario.isEnabled = false

        val properties = listOf(
            AudioChannelProperty(
                speedRate = binding.firstAudioChannelProperties.sliderSpeechRate.value + 50,
                pitch = binding.firstAudioChannelProperties.sliderSpeechPitch.value - 20,
                timbre = (binding.firstAudioChannelProperties.sliderSpeechTimbre.value + 50).toInt(),
                voice = binding.firstAudioChannelProperties.spinnerVoices.selectedItem.toString()
            ),
            AudioChannelProperty(
                speedRate = binding.secondAudioChannelProperties.sliderSpeechRate.value + 50,
                pitch = binding.secondAudioChannelProperties.sliderSpeechPitch.value - 20,
                timbre = (binding.secondAudioChannelProperties.sliderSpeechTimbre.value + 50).toInt(),
                voice = binding.secondAudioChannelProperties.spinnerVoices.selectedItem.toString()
            ),
            AudioChannelProperty(
                speedRate = binding.thirdAudioChannelProperties.sliderSpeechRate.value + 50,
                pitch = binding.thirdAudioChannelProperties.sliderSpeechPitch.value - 20,
                timbre = (binding.thirdAudioChannelProperties.sliderSpeechTimbre.value + 50).toInt(),
                voice = binding.thirdAudioChannelProperties.spinnerVoices.selectedItem.toString()
            ),
            AudioChannelProperty(
                speedRate = binding.fourthAudioChannelProperties.sliderSpeechRate.value + 50,
                pitch = binding.fourthAudioChannelProperties.sliderSpeechPitch.value - 20,
                timbre = (binding.fourthAudioChannelProperties.sliderSpeechTimbre.value + 50).toInt(),
                voice = binding.fourthAudioChannelProperties.spinnerVoices.selectedItem.toString()
            ),
        )

        val numberOfVoices = binding.sliderVoicesConfiguration.value.toInt()
        val filteredAudioProperties = properties.take(numberOfVoices)

        //TODO do this programatically
        val sampleText = when (numberOfVoices) {
            1 -> {
                listOf(
                    "A história de Portugal como nação europeia remonta à Baixa Idade Média, quando o condado Portucalense se tornou autónomo do reino de Leão. Contudo a história da presença humana no território correspondente a Portugal começou muito antes. A pré-história regista os primeiros hominídeos há cerca de 500 mil anos. O território foi visitado por diversos povos: fenícios que fundaram feitorias, mais tarde substituídos por cartagineses. Povos celtas estabeleceram-se e misturaram-se com os nativos. No século III a.C. era habitado por vários povos, quando se deu a invasão romana da Península Ibérica."
                )
            }
            2 -> {
                listOf(
                    "A história de Portugal como nação europeia remonta à Baixa Idade Média, quando o condado Portucalense se tornou autónomo do reino de Leão. Contudo a história da presença humana no território correspondente a Portugal começou muito antes.",
                    "A pré-história regista os primeiros hominídeos há cerca de 500 mil anos. O território foi visitado por diversos povos: fenícios que fundaram feitorias, mais tarde substituídos por cartagineses. Povos celtas estabeleceram-se e misturaram-se com os nativos. No século III a.C. era habitado por vários povos, quando se deu a invasão romana da Península Ibérica."
                )
            }
            3 -> {
                listOf(
                    "A história de Portugal como nação europeia remonta à Baixa Idade Média, quando o condado Portucalense se tornou autónomo do reino de Leão. Contudo a história da presença humana no território correspondente a Portugal começou muito antes.",
                    "A pré-história regista os primeiros hominídeos há cerca de 500 mil anos. O território foi visitado por diversos povos: fenícios que fundaram feitorias, mais tarde substituídos por cartagineses.",
                    "Povos celtas estabeleceram-se e misturaram-se com os nativos. No século III a.C. era habitado por vários povos, quando se deu a invasão romana da Península Ibérica."
                )
            }
            else -> {
                listOf(
                    "A história de Portugal como nação europeia remonta à Baixa Idade Média, quando o condado Portucalense se tornou autónomo do reino de Leão",
                    "Contudo a história da presença humana no território correspondente a Portugal começou muito antes.",
                    "A pré-história regista os primeiros hominídeos há cerca de 500 mil anos. O território foi visitado por diversos povos: fenícios que fundaram feitorias, mais tarde substituídos por cartagineses.",
                    "Povos celtas estabeleceram-se e misturaram-se com os nativos. No século III a.C. era habitado por vários povos, quando se deu a invasão romana da Península Ibérica."
                )
            }
        }

        scope.launch {

            filteredAudioProperties.forEachIndexed { index, audioChannelProperty ->

                val synthesizeSpeechPresignRequest =
                    SynthesizeSpeechPresignRequest() // Set text to synthesize.
                        .withText(
                            sampleText[index].convertToSsml(
                                speedRate = audioChannelProperty.speedRate,
                                pitch = audioChannelProperty.pitch,
                                timbre = audioChannelProperty.timbre
                            )
                        ) // Set voice selected by the user.
                        .withVoiceId(audioChannelProperty.voice) // Set format to MP3.
                        .withTextType(TextType.Ssml) // Set format to ssml (to configure audio properties)
                        .withOutputFormat(OutputFormat.Mp3)

                val presignedSynthesizeSpeechUrl =
                    client?.getPresignedSynthesizeSpeechUrl(synthesizeSpeechPresignRequest)


                when (index) {
                    0 -> setFirstMediaPlayer(presignedSynthesizeSpeechUrl = presignedSynthesizeSpeechUrl)
                    1 -> setSecondMediaPlayer(presignedSynthesizeSpeechUrl = presignedSynthesizeSpeechUrl)
                    2 -> setThirdMediaPlayer(presignedSynthesizeSpeechUrl = presignedSynthesizeSpeechUrl)
                    3 -> setFourthMediaPlayer(presignedSynthesizeSpeechUrl = presignedSynthesizeSpeechUrl)
                }
            }
        }
    }

    private fun setFirstMediaPlayer(presignedSynthesizeSpeechUrl: URL?) {
        if (mediaPlayer1?.isPlaying == true) {
            setupFirstNewMediaPlayer()
        }
        mediaPlayer1?.setDataSource(presignedSynthesizeSpeechUrl.toString())
        mediaPlayer1?.prepareAsync()
    }

    private fun setSecondMediaPlayer(presignedSynthesizeSpeechUrl: URL?) {
        if (mediaPlayer2?.isPlaying == true) {
            setupSecondNewMediaPlayer()
        }
        mediaPlayer2?.setDataSource(presignedSynthesizeSpeechUrl.toString())
        mediaPlayer2?.prepareAsync()
    }

    private fun setThirdMediaPlayer(presignedSynthesizeSpeechUrl: URL?) {
        if (mediaPlayer3?.isPlaying == true) {
            setupThirdNewMediaPlayer()
        }
        mediaPlayer3?.setDataSource(presignedSynthesizeSpeechUrl.toString())
        mediaPlayer3?.prepareAsync()
    }

    private fun setFourthMediaPlayer(presignedSynthesizeSpeechUrl: URL?) {
        if (mediaPlayer4?.isPlaying == true) {
            setupFourthNewMediaPlayer()
        }
        mediaPlayer4?.setDataSource(presignedSynthesizeSpeechUrl.toString())
        mediaPlayer4?.prepareAsync()
    }

    private fun setupFirstNewMediaPlayer() {
        mediaPlayer1 = MediaPlayer()
        mediaPlayer1?.setOnCompletionListener { mp ->
            mp.release()
            setupFirstNewMediaPlayer()

            binding.btnPlayScenario.isEnabled = true
        }
        mediaPlayer1?.setOnPreparedListener { mp ->
            mp.start()
        }
        mediaPlayer1?.setOnErrorListener { _, _, _ ->
            binding.btnPlayScenario.isEnabled = true
            false
        }
    }

    private fun setupSecondNewMediaPlayer() {
        mediaPlayer2 = MediaPlayer()
        mediaPlayer2?.setOnCompletionListener { mp ->
            mp.release()
            setupSecondNewMediaPlayer()

            binding.btnPlayScenario.isEnabled = true
        }
        mediaPlayer2?.setOnPreparedListener { mp ->
            mp.start()
        }
        mediaPlayer2?.setOnErrorListener { _, _, _ ->
            binding.btnPlayScenario.isEnabled = true
            false
        }
    }

    private fun setupThirdNewMediaPlayer() {
        mediaPlayer3 = MediaPlayer()
        mediaPlayer3?.setOnCompletionListener { mp ->
            mp.release()
            setupThirdNewMediaPlayer()

            binding.btnPlayScenario.isEnabled = true
        }
        mediaPlayer3?.setOnPreparedListener { mp ->
            mp.start()
        }
        mediaPlayer3?.setOnErrorListener { _, _, _ ->
            binding.btnPlayScenario.isEnabled = true
            false
        }
    }

    private fun setupFourthNewMediaPlayer() {
        mediaPlayer4 = MediaPlayer()
        mediaPlayer4?.setOnCompletionListener { mp ->
            mp.release()
            setupFourthNewMediaPlayer()

            binding.btnPlayScenario.isEnabled = true
        }
        mediaPlayer4?.setOnPreparedListener { mp ->
            mp.start()
        }
        mediaPlayer4?.setOnErrorListener { _, _, _ ->
            binding.btnPlayScenario.isEnabled = true
            false
        }
    }

/*    private fun playOneDocumentSource() {
        oneDocumentSourceMediaPlayer.seekTo(0)
        oneDocumentSourceMediaPlayer.start()
    }

    private fun stopOneDocumentSource() {
        if (oneDocumentSourceMediaPlayer.isPlaying) {
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
        if (twoDocumentSourcePart1MediaPlayer.isPlaying) {
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
        if (threeDocumentSourcePart1MediaPlayer.isPlaying) {
            threeDocumentSourcePart1MediaPlayer.pause()
            threeDocumentSourcePart2MediaPlayer.pause()
            threeDocumentSourcePart3MediaPlayer.pause()
        }
    }*/

    override fun onPause() {
/*        oneDocumentSourceMediaPlayer.stop()
        twoDocumentSourcePart1MediaPlayer.stop()
        twoDocumentSourcePart2MediaPlayer.stop()
        threeDocumentSourcePart1MediaPlayer.stop()
        threeDocumentSourcePart2MediaPlayer.stop()
        threeDocumentSourcePart3MediaPlayer.stop()*/

        mediaPlayer1?.stop()
        mediaPlayer1?.release()
        mediaPlayer2?.stop()
        mediaPlayer2?.release()
        mediaPlayer3?.stop()
        mediaPlayer3?.release()
        mediaPlayer4?.stop()
        mediaPlayer4?.release()
        super.onPause()
    }

    companion object {
        private const val sampleText = "Este é um exemplo de um texto que vai ser dividido"
        private const val TAG = "Text to Speech"
    }
}