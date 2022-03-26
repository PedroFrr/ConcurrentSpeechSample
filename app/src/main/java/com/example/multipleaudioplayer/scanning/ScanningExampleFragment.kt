package com.example.multipleaudioplayer.scanning

import android.content.ContextWrapper
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
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
import com.google.vr.sdk.audio.GvrAudioEngine
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URL

class ScanningExampleFragment : Fragment(R.layout.layout_scanning_example) {

    private val binding by viewBinding(LayoutScanningExampleBinding::bind)

    private val scope = CoroutineScope(Dispatchers.IO)

    var mediaPlayer1: MediaPlayer? = null

    var mediaPlayer2: MediaPlayer? = null

    var mediaPlayer3: MediaPlayer? = null

    var mediaPlayer4: MediaPlayer? = null

    var client: AmazonPollyPresigningClient? = null

    private var job: Job? = null

    private var oneVoiceId = GvrAudioEngine.INVALID_ID
    private var twoVoicePartOneId = GvrAudioEngine.INVALID_ID
    private var twoVoicePartTwoId = GvrAudioEngine.INVALID_ID
    private var threeVoicePartOneId = GvrAudioEngine.INVALID_ID
    private var threeVoicePartTwoId = GvrAudioEngine.INVALID_ID
    private var threeVoicePartThreeId = GvrAudioEngine.INVALID_ID
    private var fourVoicePartOneId = GvrAudioEngine.INVALID_ID
    private var fourVoicePartTwoId = GvrAudioEngine.INVALID_ID
    private var fourVoicePartThreeId = GvrAudioEngine.INVALID_ID
    private var fourVoicePartFourId = GvrAudioEngine.INVALID_ID

    private val audioEngine by lazy {
        GvrAudioEngine(requireActivity(), GvrAudioEngine.RenderingMode.BINAURAL_HIGH_QUALITY)
    }

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

        binding.btnPlaySpatialScenario.setOnClickListener {
            playNotificationExampleWithSpatialization()
        }
    }

    private fun playNotificationExampleWithSpatialization() {
        binding.btnPlaySpatialScenario.isEnabled = false
        binding.btnPlayScenario.isEnabled = false

        scope.launch {
            withContext(Dispatchers.IO) {
                when (binding.sliderVoicesConfiguration.value.toInt()) {
                    1 -> {
                        audioEngine.preloadSoundFile(SCANNING_ONE_VOICE)
                        oneVoiceId = audioEngine.createSoundObject(SCANNING_ONE_VOICE)

                        audioEngine.setSoundObjectPosition(oneVoiceId, 0.0f, 0.0f, 0.0f)
                        audioEngine.playSound(oneVoiceId, false)
                    }
                    2 -> {
                        audioEngine.preloadSoundFile(SCANNING_TWO_VOICES_PART_ONE)
                        audioEngine.preloadSoundFile(SCANNING_TWO_VOICES_PART_TWO)

                        twoVoicePartOneId = audioEngine.createSoundObject(SCANNING_TWO_VOICES_PART_ONE)
                        twoVoicePartTwoId = audioEngine.createSoundObject(SCANNING_TWO_VOICES_PART_TWO)

                        audioEngine.setSoundObjectPosition(twoVoicePartOneId, -8.0f, 0.0f, 0.0f)
                        audioEngine.setSoundObjectPosition(twoVoicePartTwoId, 8.0f, 0.0f, 0.0f)

                        audioEngine.playSound(twoVoicePartOneId, false)
                        audioEngine.playSound(twoVoicePartTwoId, false)
                    }
                    3 -> {
                        audioEngine.preloadSoundFile(SCANNING_THREE_VOICE_PART_ONE)
                        audioEngine.preloadSoundFile(SCANNING_THREE_VOICE_PART_TWO)
                        audioEngine.preloadSoundFile(SCANNING_THREE_VOICE_PART_THREE)

                        threeVoicePartOneId = audioEngine.createSoundObject(SCANNING_THREE_VOICE_PART_ONE)
                        threeVoicePartTwoId = audioEngine.createSoundObject(SCANNING_THREE_VOICE_PART_TWO)
                        threeVoicePartThreeId = audioEngine.createSoundObject(SCANNING_THREE_VOICE_PART_THREE)

                        audioEngine.setSoundObjectPosition(threeVoicePartOneId, -8.0f, 0.0f, 0.0f)
                        audioEngine.setSoundObjectPosition(threeVoicePartTwoId, 0.0f, 0.0f, 0.0f)
                        audioEngine.setSoundObjectPosition(threeVoicePartThreeId, 8.0f, 0.0f, 0.0f)

                        audioEngine.playSound(threeVoicePartOneId, false)
                        audioEngine.playSound(threeVoicePartTwoId, false)
                        audioEngine.playSound(threeVoicePartThreeId, false)
                    }
                    4 -> {
                        audioEngine.preloadSoundFile(SCANNING_FOUR_VOICE_PART_ONE)
                        audioEngine.preloadSoundFile(SCANNING_FOUR_VOICE_PART_TWO)
                        audioEngine.preloadSoundFile(SCANNING_FOUR_VOICE_PART_THREE)
                        audioEngine.preloadSoundFile(SCANNING_FOUR_VOICE_PART_FOUR)

                        fourVoicePartOneId = audioEngine.createSoundObject(SCANNING_FOUR_VOICE_PART_ONE)
                        fourVoicePartTwoId = audioEngine.createSoundObject(SCANNING_FOUR_VOICE_PART_TWO)
                        fourVoicePartThreeId = audioEngine.createSoundObject(SCANNING_FOUR_VOICE_PART_THREE)
                        fourVoicePartFourId = audioEngine.createSoundObject(SCANNING_FOUR_VOICE_PART_FOUR)

                        audioEngine.setSoundObjectPosition(fourVoicePartOneId, -8.0f, 0.0f, 0.0f)
                        audioEngine.setSoundObjectPosition(fourVoicePartTwoId, 0.0f, -8.0f, 0.0f)
                        audioEngine.setSoundObjectPosition(fourVoicePartThreeId, 0.0f, 8.0f, 0.0f)
                        audioEngine.setSoundObjectPosition(fourVoicePartFourId, 8.0f, 0.0f, 0.0f)

                        audioEngine.playSound(fourVoicePartOneId, false)
                        audioEngine.playSound(fourVoicePartTwoId, false)
                        audioEngine.playSound(fourVoicePartThreeId, false)
                        audioEngine.playSound(fourVoicePartFourId, false)
                    }
                    else -> {
                        /* DO NOTHING */
                    }
                }
            }

            withContext(Dispatchers.Main) {
                binding.btnPlaySpatialScenario.isEnabled = true
                binding.btnPlayScenario.isEnabled = true
            }
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

        audioEngine.pause()

        super.onPause()
    }

    companion object {
        private const val SCANNING_ONE_VOICE = "scanning_one_voice.mp3"
        private const val SCANNING_TWO_VOICES_PART_ONE = "scanning_two_voices_part_one.mp3"
        private const val SCANNING_TWO_VOICES_PART_TWO = "scanning_two_voices_part_two.mp3"
        private const val SCANNING_THREE_VOICE_PART_ONE = "scanning_three_voice_part_one.mp3"
        private const val SCANNING_THREE_VOICE_PART_TWO = "scanning_three_voice_part_two.mp3"
        private const val SCANNING_THREE_VOICE_PART_THREE = "scanning_three_voice_part_three.mp3"
        private const val SCANNING_FOUR_VOICE_PART_ONE = "scanning_four_voice_part_one.mp3"
        private const val SCANNING_FOUR_VOICE_PART_TWO = "scanning_four_voice_part_two.mp3"
        private const val SCANNING_FOUR_VOICE_PART_THREE = "scanning_four_voice_part_three.mp3"
        private const val SCANNING_FOUR_VOICE_PART_FOUR = "scanning_four_voice_part_four.mp3"
    }
}