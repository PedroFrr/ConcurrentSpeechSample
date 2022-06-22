package com.example.multipleaudioplayer.spatialization

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.multipleaudioplayer.R
import com.example.multipleaudioplayer.databinding.LayoutSpatializationMapOneBinding
import com.google.vr.sdk.audio.GvrAudioEngine
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MapFirstPointFragment :
    Fragment(R.layout.layout_spatialization_map_one) {

    private val binding by viewBinding(LayoutSpatializationMapOneBinding::bind)

    private val scope = CoroutineScope(Dispatchers.IO)

    private var documentPartOneSourceId = GvrAudioEngine.INVALID_ID
    private var documentPartTwoSourceId = GvrAudioEngine.INVALID_ID

    private val pointZeroFcul = floatArrayOf(-1f, 0.0f, 0.0f)
    private val pointZeroPizzaria = floatArrayOf(2f, 5.0f, 0.0f)

    private val audioEngine by lazy {
        GvrAudioEngine(requireActivity(), GvrAudioEngine.RenderingMode.BINAURAL_HIGH_QUALITY)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        scope.launch {
            withContext(Dispatchers.Main) {
                binding.btnScenario.isEnabled = false
            }
            audioEngine.preloadSoundFile(POINT_ZERO_FCUL)
            audioEngine.preloadSoundFile(POINT_ZERO_PIZZARIA)

            withContext(Dispatchers.Main) {
                binding.btnScenario.isEnabled = true
            }
        }

        setupUi()
    }

    private fun setupUi() {
        binding.btnScenario.setOnClickListener {
            playPointZero()
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

    private fun playPointZero() {
        scope.launch {
            documentPartOneSourceId = audioEngine.createSoundObject(POINT_ZERO_FCUL)
            documentPartTwoSourceId = audioEngine.createSoundObject(POINT_ZERO_PIZZARIA)

            audioEngine.setSoundObjectPosition(
                documentPartOneSourceId,
                pointZeroFcul[0],
                pointZeroFcul[1],
                pointZeroFcul[2]
            )

            audioEngine.setSoundObjectPosition(
                documentPartTwoSourceId,
                pointZeroPizzaria[0],
                pointZeroPizzaria[1],
                pointZeroPizzaria[2]
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
        private const val POINT_ZERO_FCUL = "fcul_100m.mp3"
        private const val POINT_ZERO_PIZZARIA = "pizzaria_500m.mp3"
    }
}