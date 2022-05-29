package com.example.multipleaudioplayer.scanning.nospatialization

import android.media.MediaPlayer
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.amazonaws.auth.CognitoCachingCredentialsProvider
import com.amazonaws.regions.Regions
import com.amazonaws.services.polly.AmazonPollyPresigningClient
import com.amazonaws.services.polly.model.OutputFormat
import com.amazonaws.services.polly.model.SynthesizeSpeechPresignRequest
import com.amazonaws.services.polly.model.TextType
import com.example.multipleaudioplayer.R
import com.example.multipleaudioplayer.databinding.LayoutScanningExampleBinding
import com.example.multipleaudioplayer.databinding.LayoutScanningNoSpatializationBinding
import com.example.multipleaudioplayer.scanning.AudioChannelProperty
import com.example.multipleaudioplayer.utils.convertToSsml
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.net.URL

class ScanningNoSpatializationExampleFragment : Fragment(R.layout.layout_scanning_no_spatialization) {

    private val binding by viewBinding(LayoutScanningNoSpatializationBinding::bind)

    private val scope = CoroutineScope(Dispatchers.IO)

    var mediaPlayer1: MediaPlayer? = null

    var mediaPlayer2: MediaPlayer? = null

    var mediaPlayer3: MediaPlayer? = null

    var mediaPlayer4: MediaPlayer? = null

    private var client: AmazonPollyPresigningClient? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initPollyClient()

        setupUi()

        setupFirstNewMediaPlayer()
        setupSecondNewMediaPlayer()
        setupThirdNewMediaPlayer()
        setupFourthNewMediaPlayer()
    }

    private fun setupUi() {
        setupSpinner()

        setupVoiceSpinners()

        binding.btnPlayScenario.setOnClickListener {
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
}