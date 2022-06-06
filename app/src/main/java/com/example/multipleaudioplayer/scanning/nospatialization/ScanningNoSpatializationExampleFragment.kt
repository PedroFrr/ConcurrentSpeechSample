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

    private var mediaPlayer1: MediaPlayer? = null

    private var mediaPlayer2: MediaPlayer? = null

    private var mediaPlayer3: MediaPlayer? = null

    private var mediaPlayer4: MediaPlayer? = null

    private var client: AmazonPollyPresigningClient? = null

    private var numberOfTimesOneVoiceButtonWasClicked = -1
    private var numberOfTimesTwoVoiceButtonWasClicked = -1
    private var numberOfTimesThreeVoiceButtonWasClicked = -1
    private var numberOfTimesFourVoiceButtonWasClicked = -1

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

        binding.cvMediaPlayerButtons.apply {
            btnStop.setOnClickListener {
                if (mediaPlayer1?.isPlaying == true) {
                    mediaPlayer1?.stop()
                    mediaPlayer1?.release()
                }

                if (mediaPlayer2?.isPlaying == true) {
                    mediaPlayer2?.stop()
                    mediaPlayer2?.release()
                }

                if (mediaPlayer3?.isPlaying == true) {
                    mediaPlayer3?.stop()
                    mediaPlayer3?.release()
                }

                if (mediaPlayer4?.isPlaying == true) {
                    mediaPlayer4?.stop()
                    mediaPlayer4?.release()
                }

                binding.btnPlayScenario.isEnabled = true
            }
            btnPlay.setOnClickListener { }
            btnPause.setOnClickListener { }
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
        val listOfAudioChannels = listOf(2, 3, 4)
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

        // set default values for spinner
        val inesVoicePosition = 1
        binding.secondAudioChannelProperties.spinnerVoices.setSelection(inesVoicePosition)
        binding.fourthAudioChannelProperties.spinnerVoices.setSelection(inesVoicePosition)
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

        when (numberOfVoices) {
            1 -> {
                if (numberOfTimesOneVoiceButtonWasClicked < 1) numberOfTimesOneVoiceButtonWasClicked++ else numberOfTimesOneVoiceButtonWasClicked = 0
            }
            2 -> {
                if (numberOfTimesTwoVoiceButtonWasClicked < 1) numberOfTimesTwoVoiceButtonWasClicked++ else numberOfTimesTwoVoiceButtonWasClicked = 0
            }
            3 -> {
                if (numberOfTimesThreeVoiceButtonWasClicked < 1) numberOfTimesThreeVoiceButtonWasClicked++ else numberOfTimesThreeVoiceButtonWasClicked = 0
            }
            else -> {
                if (numberOfTimesFourVoiceButtonWasClicked < 1) numberOfTimesFourVoiceButtonWasClicked++ else numberOfTimesFourVoiceButtonWasClicked = 0
            }
        }

        val sampleTexts = when (numberOfVoices) {
            2 -> listOf(
                listOf(
                    "A Espanha evitou, este domingo, em Praga, com uma igualdade (2-2), a derrota frente à República Checa, salvando um ponto com um golo de Iñigo Martínez aos 90 minutos do jogo da segunda ronda do grupo 2 da Liga A da Liga das Nações. Um desfecho que tirou os checos da liderança do grupo, assumida por Portugal." +
                            "Depois do empate com Portugal, a Espanha voltou a ceder frente a um adversário que manteve a invencibilidade caseira e que esteve na iminência de somar o segundo triunfo depois de ter derrotado a Suíça.",
                    "Pesek colocou a República Checa em vantagem aos quatro minutos, deixando a Espanha totalmente exposta ao jogo que mais convinha à equipa de Jaroslav Silhavy, a privilegiar a velocidade e os duelos de uma equipa mais disponível do ponto de vista atlético. " +
                            "A Espanha não baixou os braços e chegou ao empate no último minuto do tempo regulamentar, num cabeceamento de Iñigo Martínez que levou a bola a bater na trave e a passar a linha de golo antes de sair para ser confirmado pelo VAR."
                ),
                listOf(
                    "Uma equipa internacional de astrónomos revelou esta quinta-feira a primeira imagem de um buraco negro supermassivo no centro da Via Láctea - um corpo cósmico conhecido como Sagittarius A." +
                            "A imagem produzida por uma equipa global de cientistas conhecida como Event Horizon Telescope (EHT) Collaboration é a primeira confirmação visual direta da presença desse objeto.",
                    "Os buracos negros são regiões do espaço onde a força da gravidade é tão intensa que nada pode escapar, incluindo a luz." +
                            "A imagem mostra não o buraco negro, que é completamente escuro, mas o gás brilhante que envolve o fenómeno - que é quatro milhões de vezes mais massivo que o Sol, num anel brilhante de luz curvada."
                )
            )
            3 -> listOf(
                listOf(
                    "Portugal venceu esta noite a Suíça por 4-0, com o madeirense Cristiano Ronaldo a 'bisar' na partida, em jogo da segunda jornada do grupo 2 da Liga da Liga das Nações." +
                            "William Carvalho inaugurou o marcador aos 15' de jogo. Ronaldo bateu forte o livre para defesa incompleta de Kobel. Na recarga, William Carvalho apareceu solto de marcação e fez o 1-0.",
                    "Ronaldo ampliou à passagem do minuto 35'. Bela jogada coletiva com Bruno Fernandes a aparecer no espaço, após desvio de Otávio que em esforço cruzou para Diogo Jota que segurou a bola e passou atrasado para uma finalização forte de Cristiano Ronaldo.",
                    "O mesmo Ronaldo fez o 3-0, aos 39'. O madeirense apareceu solto de marcação para o terceiro de Portugal. Cancelo fixou o resultado aos 68'. Bernardo Silva serviu a desmarcação de João Cancelo, que com muita classe, atira colocado para o 4-0."
                ),
                listOf(
                    "O País de Gales qualificou-se hoje para a fase final do Mundial de futebol, 64 anos depois, ao bater em Cardiff a Ucrânia por 1-0, na final do caminho A do ‘play-off’ europeu de acesso ao Mundial2022." +
                            "Um autogolo de Yarmolenko, aos 34 minutos, a desviar um livre de Gareth Bale, selou o apuramento dos galeses, que só tinham estado no Mundial em 1958, ano em que terminaram no sexto lugar.",
                    "Num jogo muito equilibrado, ambas as equipas criaram várias oportunidades de golo, mas, muitas vezes por ação dos guarda-redes Hennessey (País de Gales) e Bushchan (Ucrânia) e outras por falta de pontaria dos avançados, não houve mais golos.",
                    "Na fase final, os galeses vão integrar o Grupo B, juntamente com Inglaterra, Irão e Estados Unidos, face aos quais se vão estrear, em 21 de novembro."
                )
            )
            else -> listOf(
                listOf(
                    "O tenista espanhol Rafael Nadal impôs-se hoje ao norueguês Casper Ruud em três ‘sets’ na final de Roland Garros, conquistando o seu 14.º título na terra batida parisiense e o 22.º em torneios do ‘Grand Slam’.",
                    "Rafa, já recordista de cetros na ‘catedral da terra batida e em majors, ampliou ainda mais a sua lenda, com um triunfo por 6-3, 6-3 e 6-0, em duas horas e 18 minutos, diante do oitavo tenista mundial, a estrear-se em finais do ‘Grand Slam’, aos 23 anos, e logo diante do seu ídolo de infância.",
                    "Com 36 anos recém-cumpridos, Nadal, número cinco da hierarquia ATP e vencedor do Open da Austrália deste ano, tem agora mais dois títulos do ‘Grand Slam’ do que o sérvio Novak Djokovic.",
                    "Depois de ter vencido pela 112.ª vez nos 115 encontros que disputou ao longo da sua carreira em Roland Garros."
                ),
                listOf(
                    "A Madeira já investiu cerca de oito milhões de euros na introdução de manuais digitais nas escolas.",
                    "A Região Autónoma da Madeira introduziu os manuais digitais pela primeira vez no ano letivo 2018/2019 e, de acordo com o governante, o processo está agora “suficientemente consolidado” ao nível do 2.º e 3.º ciclos do ensino básico.",
                    "Sobre o uso dos manuais digitais, o governante referiu que em algumas disciplinas se verifica um “aproveitamento muito mais significativo” e, por outro lado, a indisciplina na sala de aula diminuiu mais de 30%.",
                    "Cerca de 2.000 alunos ingressam por ano no 5.º ano de escolaridade e com os manuais digitais estamos a poupar o equivalente a 280 árvores."
                )
            )
        }

        val sampleText = when (numberOfVoices) {
            1 -> listOf()
            2 -> sampleTexts[numberOfTimesTwoVoiceButtonWasClicked]
            3 -> sampleTexts[numberOfTimesThreeVoiceButtonWasClicked]
            else -> sampleTexts[numberOfTimesFourVoiceButtonWasClicked]
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
        setupFirstNewMediaPlayer()
        mediaPlayer1?.setDataSource(presignedSynthesizeSpeechUrl.toString())
        mediaPlayer1?.prepareAsync()
    }

    private fun setSecondMediaPlayer(presignedSynthesizeSpeechUrl: URL?) {
        setupSecondNewMediaPlayer()
        mediaPlayer2?.setDataSource(presignedSynthesizeSpeechUrl.toString())
        mediaPlayer2?.prepareAsync()
    }

    private fun setThirdMediaPlayer(presignedSynthesizeSpeechUrl: URL?) {
        setupThirdNewMediaPlayer()
        mediaPlayer3?.setDataSource(presignedSynthesizeSpeechUrl.toString())
        mediaPlayer3?.prepareAsync()
    }

    private fun setFourthMediaPlayer(presignedSynthesizeSpeechUrl: URL?) {
        setupFourthNewMediaPlayer()
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

    override fun onPause() {
        if (mediaPlayer1?.isPlaying == true) {
            mediaPlayer1?.stop()
            mediaPlayer1?.release()
        }

        if (mediaPlayer2?.isPlaying == true) {
            mediaPlayer2?.stop()
            mediaPlayer2?.release()
        }

        if (mediaPlayer3?.isPlaying == true) {
            mediaPlayer3?.stop()
            mediaPlayer3?.release()
        }

        if (mediaPlayer4?.isPlaying == true) {
            mediaPlayer4?.stop()
            mediaPlayer4?.release()
        }
        super.onPause()
    }
}