package com.example.multipleaudioplayer.spatialization

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.multipleaudioplayer.R
import com.example.multipleaudioplayer.databinding.LayoutSpatializationMapTwoBinding
import com.google.vr.sdk.audio.GvrAudioEngine
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MapSecondPointFragment :
    Fragment(R.layout.layout_spatialization_map_two) {

    private val binding by viewBinding(LayoutSpatializationMapTwoBinding::bind)

    private val scope = CoroutineScope(Dispatchers.IO)

    private var documentPartOneSourceId = GvrAudioEngine.INVALID_ID
    private var documentPartTwoSourceId = GvrAudioEngine.INVALID_ID

    private val pointOneFcul = floatArrayOf(0.0f, -1f, 0.0f)
    private val pointOnePizzaria = floatArrayOf(3.0f, 2.0f, 0.0f)

    private val audioEngine by lazy {
        GvrAudioEngine(requireActivity(), GvrAudioEngine.RenderingMode.BINAURAL_HIGH_QUALITY)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        scope.launch {
            withContext(Dispatchers.Main) {
                binding.btnScenario.isEnabled = false
            }
            audioEngine.preloadSoundFile(POINT_ONE_FCUL)
            audioEngine.preloadSoundFile(POINT_ONE_PIZZARIA)

            withContext(Dispatchers.Main) {
                binding.btnScenario.isEnabled = true
            }
        }

        setupUi()
    }

    private fun setupUi() {
        binding.btnScenario.setOnClickListener {
            playPointOne()
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

    private fun playPointOne() {
        scope.launch {
            documentPartOneSourceId = audioEngine.createSoundObject(POINT_ONE_FCUL)
            documentPartTwoSourceId = audioEngine.createSoundObject(POINT_ONE_PIZZARIA)

            audioEngine.setSoundObjectPosition(
                documentPartOneSourceId,
                pointOneFcul[0],
                pointOneFcul[1],
                pointOneFcul[2]
            )

            audioEngine.setSoundObjectPosition(
                documentPartTwoSourceId,
                pointOnePizzaria[0],
                pointOnePizzaria[1],
                pointOnePizzaria[2]
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
        private const val POINT_ONE_FCUL = "fcul_100m.mp3"
        private const val POINT_ONE_PIZZARIA = "pizzaria_300m.mp3"
    }
}