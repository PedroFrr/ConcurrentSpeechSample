package com.example.multipleaudioplayer.spatialization

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.multipleaudioplayer.R
import com.example.multipleaudioplayer.databinding.LayoutSpatializationMapFourBinding
import com.google.vr.sdk.audio.GvrAudioEngine
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MapFourthPointFragment :
    Fragment(R.layout.layout_spatialization_map_four) {

    private val binding by viewBinding(LayoutSpatializationMapFourBinding::bind)

    private val scope = CoroutineScope(Dispatchers.IO)

    private var documentPartOneSourceId = GvrAudioEngine.INVALID_ID
    private var documentPartTwoSourceId = GvrAudioEngine.INVALID_ID

    private val pointThreeFcul = floatArrayOf(-2.0f, -3.0f, 0.0f)
    private val pointThreePizzaria = floatArrayOf(0f, 0.5f, 0.0f)

    private val audioEngine by lazy {
        GvrAudioEngine(requireActivity(), GvrAudioEngine.RenderingMode.BINAURAL_HIGH_QUALITY)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        scope.launch {
            withContext(Dispatchers.Main) {
                binding.btnScenario.isEnabled = false
            }
            audioEngine.preloadSoundFile(POINT_THREE_FCUL)
            audioEngine.preloadSoundFile(POINT_THREE_PIZZARIA)

            withContext(Dispatchers.Main) {
                binding.btnScenario.isEnabled = true
            }
        }

        setupUi()
    }

    private fun setupUi() {
        binding.btnScenario.setOnClickListener {
            playPointThree()
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

    private fun playPointThree() {
        scope.launch {
            documentPartOneSourceId = audioEngine.createSoundObject(POINT_THREE_FCUL)
            documentPartTwoSourceId = audioEngine.createSoundObject(POINT_THREE_PIZZARIA)

            audioEngine.setSoundObjectPosition(
                documentPartOneSourceId,
                pointThreeFcul[0],
                pointThreeFcul[1],
                pointThreeFcul[2]
            )

            audioEngine.setSoundObjectPosition(
                documentPartTwoSourceId,
                pointThreePizzaria[0],
                pointThreePizzaria[1],
                pointThreePizzaria[2]
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
        private const val POINT_THREE_FCUL = "fcul_500m.mp3"
        private const val POINT_THREE_PIZZARIA = "pizzaria_50m.mp3"
    }
}