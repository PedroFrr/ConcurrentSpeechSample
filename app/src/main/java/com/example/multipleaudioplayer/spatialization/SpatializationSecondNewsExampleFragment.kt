package com.example.multipleaudioplayer.spatialization

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.multipleaudioplayer.R
import com.example.multipleaudioplayer.databinding.LayoutSpatializationSecondNewsBinding
import com.google.vr.sdk.audio.GvrAudioEngine
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SpatializationSecondNewsExampleFragment :
    Fragment(R.layout.layout_spatialization_second_news) {

    private val binding by viewBinding(LayoutSpatializationSecondNewsBinding::bind)

    private val scope = CoroutineScope(Dispatchers.IO)

    private var documentPartOneSourceId = GvrAudioEngine.INVALID_ID
    private var documentPartTwoSourceId = GvrAudioEngine.INVALID_ID

    private var leftEarPosition = floatArrayOf(-8f, 0.0f, 0.0f)
    private var rightEarPosition = floatArrayOf(8f, 0.0f, 0.0f)

    private val audioEngine by lazy {
        GvrAudioEngine(requireActivity(), GvrAudioEngine.RenderingMode.BINAURAL_HIGH_QUALITY)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        scope.launch {
            withContext(Dispatchers.Main) {
                binding.btnScenario.isEnabled = false
            }
            audioEngine.preloadSoundFile(FIRST_COLUMN)
            audioEngine.preloadSoundFile(SECOND_COLUMN)

            withContext(Dispatchers.Main) {
                binding.btnScenario.isEnabled = true
            }
        }

        setupUi()
    }

    private fun setupUi() {
        binding.btnScenario.setOnClickListener {
            playSpatialization()
        }

        binding.cvMediaPlayerButtons.apply {
            btnStop.setOnClickListener {
                if (audioEngine.isSoundPlaying(documentPartOneSourceId)) audioEngine.stopSound(documentPartOneSourceId)
                if (audioEngine.isSoundPlaying(documentPartTwoSourceId)) audioEngine.stopSound(documentPartTwoSourceId)
                binding.btnScenario.isEnabled = true
            }
            btnPlay.setOnClickListener { }
            btnPause.setOnClickListener { }
        }
    }

    private fun playSpatialization() {
        scope.launch {
            documentPartOneSourceId = audioEngine.createSoundObject(FIRST_COLUMN)
            documentPartTwoSourceId = audioEngine.createSoundObject(SECOND_COLUMN)

            audioEngine.setSoundObjectPosition(
                documentPartOneSourceId,
                leftEarPosition[0],
                leftEarPosition[1],
                leftEarPosition[2]
            )

            audioEngine.setSoundObjectPosition(
                documentPartTwoSourceId,
                rightEarPosition[0],
                rightEarPosition[1],
                rightEarPosition[2]
            )

            audioEngine.playSound(documentPartOneSourceId, false)
            audioEngine.playSound(documentPartTwoSourceId, false)
        }
    }

    override fun onPause() {
        audioEngine.pause()
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        audioEngine.resume()
    }

    companion object {
        private const val FIRST_COLUMN = "scenario_spatialization_second_news_first_column_ines.mp3"
        private const val SECOND_COLUMN =
            "scenario_spatialization_second_news_second_column_cristiano.mp3"
    }
}