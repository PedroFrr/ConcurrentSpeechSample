package com.example.multipleaudioplayer.scanning.spatialization

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.multipleaudioplayer.R
import com.example.multipleaudioplayer.databinding.LayoutScanningSpatializationBinding
import com.google.vr.sdk.audio.GvrAudioEngine
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ScanningSpatializationExampleFragment : Fragment(R.layout.layout_scanning_spatialization) {

    private val binding by viewBinding(LayoutScanningSpatializationBinding::bind)

    private val scope = CoroutineScope(Dispatchers.IO)

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

    private var numberOfTimesOneVoiceButtonWasClicked = -1
    private var numberOfTimesTwoVoiceButtonWasClicked = -1
    private var numberOfTimesThreeVoiceButtonWasClicked = -1
    private var numberOfTimesFourVoiceButtonWasClicked = -1

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        scope.launch {
            withContext(Dispatchers.Main) {
                toggleButtons(false)
            }

            awaitAll(
                async { audioEngine.preloadSoundFile(SCANNING_ONE_VOICE) },
                async { audioEngine.preloadSoundFile(SCANNING_TWO_VOICES_FIRST_NEWS_PART_ONE) },
                async { audioEngine.preloadSoundFile(SCANNING_TWO_VOICES_FIRST_NEWS_PART_TWO) },
                async { audioEngine.preloadSoundFile(SCANNING_TWO_VOICES_SECOND_NEWS_PART_ONE) },
                async { audioEngine.preloadSoundFile(SCANNING_TWO_VOICES_SECOND_NEWS_PART_TWO) },
                async { audioEngine.preloadSoundFile(SCANNING_THREE_VOICE_FIRST_NEWS_PART_ONE) },
                async { audioEngine.preloadSoundFile(SCANNING_THREE_VOICE_FIRST_NEWS_PART_TWO) },
                async { audioEngine.preloadSoundFile(SCANNING_THREE_VOICE_FIRST_NEWS_PART_THREE) },
                async { audioEngine.preloadSoundFile(SCANNING_THREE_VOICE_SECOND_NEWS_PART_ONE) },
                async { audioEngine.preloadSoundFile(SCANNING_THREE_VOICE_SECOND_NEWS_PART_TWO) },
                async { audioEngine.preloadSoundFile(SCANNING_THREE_VOICE_SECOND_NEWS_PART_THREE) },
                async { audioEngine.preloadSoundFile(SCANNING_FOUR_VOICE_FIRST_NEWS_PART_ONE) },
                async { audioEngine.preloadSoundFile(SCANNING_FOUR_VOICE_FIRST_NEWS_PART_TWO) },
                async { audioEngine.preloadSoundFile(SCANNING_FOUR_VOICE_FIRST_NEWS_PART_THREE) },
                async { audioEngine.preloadSoundFile(SCANNING_FOUR_VOICE_FIRST_NEWS_PART_FOUR) },
                async { audioEngine.preloadSoundFile(SCANNING_FOUR_VOICE_SECOND_NEWS_PART_ONE) },
                async { audioEngine.preloadSoundFile(SCANNING_FOUR_VOICE_SECOND_NEWS_PART_TWO) },
                async { audioEngine.preloadSoundFile(SCANNING_FOUR_VOICE_SECOND_NEWS_PART_THREE) },
                async { audioEngine.preloadSoundFile(SCANNING_FOUR_VOICE_SECOND_NEWS_PART_FOUR) },
            )

            withContext(Dispatchers.Main) {
                toggleButtons(true)
            }
        }

        setupUi()
    }

    private fun setupUi() {
        binding.btnSpatialScenarioOneVoice.setOnClickListener {
            playSpatializationWithNumberOfVoices(numberOfVoices = 1)
        }

        binding.btnSpatialScenarioTwoVoices.setOnClickListener {
            playSpatializationWithNumberOfVoices(numberOfVoices = 2)
        }

        binding.btnSpatialScenarioThreeVoices.setOnClickListener {
            playSpatializationWithNumberOfVoices(numberOfVoices = 3)
        }

        binding.btnSpatialScenarioFourVoices.setOnClickListener {
            playSpatializationWithNumberOfVoices(numberOfVoices = 4)
        }

        binding.cvMediaPlayerButtons.apply {
            btnStop.setOnClickListener {
                if (audioEngine.isSoundPlaying(oneVoiceId)) audioEngine.stopSound(oneVoiceId)
                if (audioEngine.isSoundPlaying(twoVoicePartOneId)) audioEngine.stopSound(twoVoicePartOneId)
                if (audioEngine.isSoundPlaying(twoVoicePartTwoId)) audioEngine.stopSound(twoVoicePartTwoId)
                if (audioEngine.isSoundPlaying(threeVoicePartOneId)) audioEngine.stopSound(threeVoicePartOneId)
                if (audioEngine.isSoundPlaying(threeVoicePartTwoId)) audioEngine.stopSound(threeVoicePartTwoId)
                if (audioEngine.isSoundPlaying(threeVoicePartThreeId)) audioEngine.stopSound(threeVoicePartThreeId)
                if (audioEngine.isSoundPlaying(fourVoicePartOneId)) audioEngine.stopSound(fourVoicePartOneId)
                if (audioEngine.isSoundPlaying(fourVoicePartTwoId)) audioEngine.stopSound(fourVoicePartTwoId)
                if (audioEngine.isSoundPlaying(fourVoicePartThreeId)) audioEngine.stopSound(fourVoicePartThreeId)
                if (audioEngine.isSoundPlaying(fourVoicePartFourId)) audioEngine.stopSound(fourVoicePartFourId)

                toggleButtons(isEnable = true)
            }
            btnPlay.setOnClickListener { }
            btnPause.setOnClickListener { }
        }
    }

    private fun playSpatializationWithNumberOfVoices(numberOfVoices: Int) {
        toggleButtons(isEnable = false)

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

        when (numberOfVoices) {
            1 -> {
                oneVoiceId = audioEngine.createSoundObject(SCANNING_ONE_VOICE)

                audioEngine.setSoundObjectPosition(oneVoiceId, 0.0f, 0.0f, 0.0f)
                audioEngine.playSound(oneVoiceId, false)
            }
            2 -> {
                val (twoVoicesPartOneId, twoVoicesPartTwoId) = if (numberOfTimesTwoVoiceButtonWasClicked == 0)
                    Pair(SCANNING_TWO_VOICES_FIRST_NEWS_PART_ONE, SCANNING_TWO_VOICES_FIRST_NEWS_PART_TWO) else
                    Pair(SCANNING_TWO_VOICES_SECOND_NEWS_PART_ONE, SCANNING_TWO_VOICES_SECOND_NEWS_PART_TWO)

                twoVoicePartOneId =
                    audioEngine.createSoundObject(twoVoicesPartOneId)
                twoVoicePartTwoId =
                    audioEngine.createSoundObject(twoVoicesPartTwoId)

                audioEngine.setSoundObjectPosition(twoVoicePartOneId, -8.0f, 0.0f, 0.0f)
                audioEngine.setSoundObjectPosition(twoVoicePartTwoId, 8.0f, 0.0f, 0.0f)

                audioEngine.playSound(twoVoicePartOneId, false)
                audioEngine.playSound(twoVoicePartTwoId, false)
            }
            3 -> {
                val (threeVoicesPartOneId, threeVoicesPartTwoId, threeVoicesPartThreeId) = if (numberOfTimesThreeVoiceButtonWasClicked == 0)
                    Triple(SCANNING_THREE_VOICE_FIRST_NEWS_PART_ONE, SCANNING_THREE_VOICE_FIRST_NEWS_PART_TWO, SCANNING_THREE_VOICE_FIRST_NEWS_PART_THREE) else
                    Triple(SCANNING_THREE_VOICE_SECOND_NEWS_PART_ONE, SCANNING_THREE_VOICE_SECOND_NEWS_PART_TWO, SCANNING_THREE_VOICE_SECOND_NEWS_PART_THREE)

                threeVoicePartOneId =
                    audioEngine.createSoundObject(threeVoicesPartOneId)
                threeVoicePartTwoId =
                    audioEngine.createSoundObject(threeVoicesPartTwoId)
                threeVoicePartThreeId =
                    audioEngine.createSoundObject(threeVoicesPartThreeId)

                audioEngine.setSoundObjectPosition(threeVoicePartOneId, -8.0f, 0.0f, 0.0f)
                audioEngine.setSoundObjectPosition(threeVoicePartTwoId, 0.0f, 0.0f, 0.0f)
                audioEngine.setSoundObjectPosition(threeVoicePartThreeId, 8.0f, 0.0f, 0.0f)

                audioEngine.playSound(threeVoicePartOneId, false)
                audioEngine.playSound(threeVoicePartTwoId, false)
                audioEngine.playSound(threeVoicePartThreeId, false)
            }
            4 -> {
                val (fourVoicesPartOneId, fourVoicesPartTwoId, fourVoicesPartThreeId, fourVoicesPartFourId) = if (numberOfTimesFourVoiceButtonWasClicked == 0)
                    Quadruple(
                        SCANNING_FOUR_VOICE_FIRST_NEWS_PART_ONE,
                        SCANNING_FOUR_VOICE_FIRST_NEWS_PART_TWO,
                        SCANNING_FOUR_VOICE_FIRST_NEWS_PART_THREE,
                        SCANNING_FOUR_VOICE_FIRST_NEWS_PART_FOUR
                    ) else
                    Quadruple(SCANNING_FOUR_VOICE_SECOND_NEWS_PART_ONE, SCANNING_FOUR_VOICE_SECOND_NEWS_PART_TWO, SCANNING_FOUR_VOICE_SECOND_NEWS_PART_THREE, SCANNING_FOUR_VOICE_SECOND_NEWS_PART_FOUR)

                fourVoicePartOneId =
                    audioEngine.createSoundObject(fourVoicesPartOneId)
                fourVoicePartTwoId =
                    audioEngine.createSoundObject(fourVoicesPartTwoId)
                fourVoicePartThreeId =
                    audioEngine.createSoundObject(fourVoicesPartThreeId)
                fourVoicePartFourId =
                    audioEngine.createSoundObject(fourVoicesPartFourId)

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

        toggleButtons(true)
    }

    private fun toggleButtons(isEnable: Boolean) {
        binding.apply {
            btnSpatialScenarioOneVoice.isEnabled = isEnable
            btnSpatialScenarioTwoVoices.isEnabled = isEnable
            btnSpatialScenarioThreeVoices.isEnabled = isEnable
            btnSpatialScenarioFourVoices.isEnabled = isEnable
        }
    }

    override fun onPause() {
        if (audioEngine.isSoundPlaying(oneVoiceId)) audioEngine.stopSound(oneVoiceId)
        if (audioEngine.isSoundPlaying(twoVoicePartOneId)) audioEngine.stopSound(twoVoicePartOneId)
        if (audioEngine.isSoundPlaying(twoVoicePartTwoId)) audioEngine.stopSound(twoVoicePartTwoId)
        if (audioEngine.isSoundPlaying(threeVoicePartOneId)) audioEngine.stopSound(threeVoicePartOneId)
        if (audioEngine.isSoundPlaying(threeVoicePartTwoId)) audioEngine.stopSound(threeVoicePartTwoId)
        if (audioEngine.isSoundPlaying(threeVoicePartThreeId)) audioEngine.stopSound(threeVoicePartThreeId)
        if (audioEngine.isSoundPlaying(fourVoicePartOneId)) audioEngine.stopSound(fourVoicePartOneId)
        if (audioEngine.isSoundPlaying(fourVoicePartTwoId)) audioEngine.stopSound(fourVoicePartTwoId)
        if (audioEngine.isSoundPlaying(fourVoicePartThreeId)) audioEngine.stopSound(fourVoicePartThreeId)
        if (audioEngine.isSoundPlaying(fourVoicePartFourId)) audioEngine.stopSound(fourVoicePartFourId)

        super.onPause()
    }

    companion object {
        private const val SCANNING_ONE_VOICE = "scanning_one_voice.mp3"

        private const val SCANNING_TWO_VOICES_FIRST_NEWS_PART_ONE = "two_voices_part_one_cristiano.mp3"
        private const val SCANNING_TWO_VOICES_FIRST_NEWS_PART_TWO = "two_voices_part_two_ines.mp3"
        private const val SCANNING_TWO_VOICES_SECOND_NEWS_PART_ONE = "two_voices_part_one_ines.mp3"
        private const val SCANNING_TWO_VOICES_SECOND_NEWS_PART_TWO = "two_voices_part_two_cristiano.mp3"

        private const val SCANNING_THREE_VOICE_FIRST_NEWS_PART_ONE = "three_voices_part_one_ines.mp3"
        private const val SCANNING_THREE_VOICE_FIRST_NEWS_PART_TWO = "three_voices_part_two_joao.mp3"
        private const val SCANNING_THREE_VOICE_FIRST_NEWS_PART_THREE = "three_voices_part_three_maria.mp3"
        private const val SCANNING_THREE_VOICE_SECOND_NEWS_PART_ONE = "three_voices_part_one_maria.mp3"
        private const val SCANNING_THREE_VOICE_SECOND_NEWS_PART_TWO = "three_voices_part_two_ines.mp3"
        private const val SCANNING_THREE_VOICE_SECOND_NEWS_PART_THREE = "three_voices_part_three_joao.mp3"

        private const val SCANNING_FOUR_VOICE_FIRST_NEWS_PART_ONE = "four_voices_part_one_ines.mp3"
        private const val SCANNING_FOUR_VOICE_FIRST_NEWS_PART_TWO = "four_voices_part_two_cristiano.mp3"
        private const val SCANNING_FOUR_VOICE_FIRST_NEWS_PART_THREE = "four_voices_part_three_joao.mp3"
        private const val SCANNING_FOUR_VOICE_FIRST_NEWS_PART_FOUR = "four_voices_part_four_maria.mp3"
        private const val SCANNING_FOUR_VOICE_SECOND_NEWS_PART_ONE = "four_voices_part_one_maria.mp3"
        private const val SCANNING_FOUR_VOICE_SECOND_NEWS_PART_TWO = "four_voices_part_two_joao.mp3"
        private const val SCANNING_FOUR_VOICE_SECOND_NEWS_PART_THREE = "four_voices_part_three_cristiano.mp3"
        private const val SCANNING_FOUR_VOICE_SECOND_NEWS_PART_FOUR = "four_voices_part_four_ines.mp3"

    }
}

data class Quadruple(
    val x: String,
    val y: String,
    val z: String,
    val a: String
)