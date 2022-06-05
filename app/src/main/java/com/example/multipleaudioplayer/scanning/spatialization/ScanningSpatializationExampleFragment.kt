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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        scope.launch {
            withContext(Dispatchers.Main) {
                toggleButtons(false)
            }

            audioEngine.preloadSoundFile(SCANNING_ONE_VOICE)

            audioEngine.preloadSoundFile(SCANNING_TWO_VOICES_PART_ONE)
            audioEngine.preloadSoundFile(SCANNING_TWO_VOICES_PART_TWO)

            audioEngine.preloadSoundFile(SCANNING_THREE_VOICE_PART_ONE)
            audioEngine.preloadSoundFile(SCANNING_THREE_VOICE_PART_TWO)
            audioEngine.preloadSoundFile(SCANNING_THREE_VOICE_PART_THREE)

            audioEngine.preloadSoundFile(SCANNING_FOUR_VOICE_PART_ONE)
            audioEngine.preloadSoundFile(SCANNING_FOUR_VOICE_PART_TWO)
            audioEngine.preloadSoundFile(SCANNING_FOUR_VOICE_PART_THREE)
            audioEngine.preloadSoundFile(SCANNING_FOUR_VOICE_PART_FOUR)

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
                oneVoiceId = audioEngine.createSoundObject(SCANNING_ONE_VOICE)

                audioEngine.setSoundObjectPosition(oneVoiceId, 0.0f, 0.0f, 0.0f)
                audioEngine.playSound(oneVoiceId, false)
            }
            2 -> {
                twoVoicePartOneId =
                    audioEngine.createSoundObject(SCANNING_TWO_VOICES_PART_ONE)
                twoVoicePartTwoId =
                    audioEngine.createSoundObject(SCANNING_TWO_VOICES_PART_TWO)

                audioEngine.setSoundObjectPosition(twoVoicePartOneId, -8.0f, 0.0f, 0.0f)
                audioEngine.setSoundObjectPosition(twoVoicePartTwoId, 8.0f, 0.0f, 0.0f)

                audioEngine.playSound(twoVoicePartOneId, false)
                audioEngine.playSound(twoVoicePartTwoId, false)
            }
            3 -> {
                threeVoicePartOneId =
                    audioEngine.createSoundObject(SCANNING_THREE_VOICE_PART_ONE)
                threeVoicePartTwoId =
                    audioEngine.createSoundObject(SCANNING_THREE_VOICE_PART_TWO)
                threeVoicePartThreeId =
                    audioEngine.createSoundObject(SCANNING_THREE_VOICE_PART_THREE)

                audioEngine.setSoundObjectPosition(threeVoicePartOneId, -8.0f, 0.0f, 0.0f)
                audioEngine.setSoundObjectPosition(threeVoicePartTwoId, 0.0f, 0.0f, 0.0f)
                audioEngine.setSoundObjectPosition(threeVoicePartThreeId, 8.0f, 0.0f, 0.0f)

                audioEngine.playSound(threeVoicePartOneId, false)
                audioEngine.playSound(threeVoicePartTwoId, false)
                audioEngine.playSound(threeVoicePartThreeId, false)
            }
            4 -> {
                fourVoicePartOneId =
                    audioEngine.createSoundObject(SCANNING_FOUR_VOICE_PART_ONE)
                fourVoicePartTwoId =
                    audioEngine.createSoundObject(SCANNING_FOUR_VOICE_PART_TWO)
                fourVoicePartThreeId =
                    audioEngine.createSoundObject(SCANNING_FOUR_VOICE_PART_THREE)
                fourVoicePartFourId =
                    audioEngine.createSoundObject(SCANNING_FOUR_VOICE_PART_FOUR)

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