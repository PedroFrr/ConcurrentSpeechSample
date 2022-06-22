package com.example.multipleaudioplayer.spatialization

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.multipleaudioplayer.R
import com.example.multipleaudioplayer.databinding.LayoutSpatializationMapThreeBinding
import com.google.vr.sdk.audio.GvrAudioEngine
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MapThirdPointFragment :
    Fragment(R.layout.layout_spatialization_map_three) {

    private val binding by viewBinding(LayoutSpatializationMapThreeBinding::bind)

    private val scope = CoroutineScope(Dispatchers.IO)

    private var documentPartOneSourceId = GvrAudioEngine.INVALID_ID
    private var documentPartTwoSourceId = GvrAudioEngine.INVALID_ID

    private val pointTwoFcul = floatArrayOf(0.0f, -3.0f, 0.0f)
    private val pointTwoPizzaria = floatArrayOf(2f, 0.0f, 0.0f)

    private val audioEngine by lazy {
        GvrAudioEngine(requireActivity(), GvrAudioEngine.RenderingMode.BINAURAL_HIGH_QUALITY)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        scope.launch {
            withContext(Dispatchers.Main) {
                binding.btnScenario.isEnabled = false
            }
            audioEngine.preloadSoundFile(POINT_TWO_FCUL)
            audioEngine.preloadSoundFile(POINT_TWO_PIZZARIA)

            withContext(Dispatchers.Main) {
                binding.btnScenario.isEnabled = true
            }
        }

        setupUi()
    }

    private fun setupUi() {
        binding.btnScenario.setOnClickListener {
            playPointTwo()
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

    private fun playPointTwo() {
        scope.launch {
            documentPartOneSourceId = audioEngine.createSoundObject(POINT_TWO_FCUL)
            documentPartTwoSourceId = audioEngine.createSoundObject(POINT_TWO_PIZZARIA)

            audioEngine.setSoundObjectPosition(
                documentPartOneSourceId,
                pointTwoFcul[0],
                pointTwoFcul[1],
                pointTwoFcul[2]
            )

            audioEngine.setSoundObjectPosition(
                documentPartTwoSourceId,
                pointTwoPizzaria[0],
                pointTwoPizzaria[1],
                pointTwoPizzaria[2]
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
        private const val POINT_TWO_FCUL = "fcul_300m.mp3"
        private const val POINT_TWO_PIZZARIA = "pizzaria_200m.mp3"
    }
}