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
import kotlinx.coroutines.delay
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

    private var pointOneFnacSourceId = GvrAudioEngine.INVALID_ID
    private var pointOneZaraSourceId = GvrAudioEngine.INVALID_ID
    private var pointOneWortenSourceId = GvrAudioEngine.INVALID_ID
    private var pointOneSpringfieldSourceId = GvrAudioEngine.INVALID_ID

    private val pointOneFnac = floatArrayOf(-10.0f, 0.0f, 10.0f)
    private val pointOneZara = floatArrayOf(-50.0f, 20.0f, 50.0f)
    private val pointOneWorten = floatArrayOf(0.0f, 100.0f, 100.0f)
    private val pointOneSpringfield = floatArrayOf(50.0f, 20.0f, 50.0f)

    private val audioEngine by lazy {
        GvrAudioEngine(requireActivity(), GvrAudioEngine.RenderingMode.BINAURAL_HIGH_QUALITY)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        scope.launch {
            withContext(Dispatchers.Main) {
                binding.btnScenario.isEnabled = false
                binding.btnScenarioSequential.isEnabled = false
            }
            audioEngine.preloadSoundFile(POINT_ZERO_FCUL)
            audioEngine.preloadSoundFile(POINT_ZERO_PIZZARIA)

            audioEngine.preloadSoundFile(POINT_ONE_FNAC)
            audioEngine.preloadSoundFile(POINT_ONE_ZARA)
            audioEngine.preloadSoundFile(POINT_ONE_WORTEN)
            audioEngine.preloadSoundFile(POINT_ONE_SPRINGFIELD)

            withContext(Dispatchers.Main) {
                binding.btnScenario.isEnabled = true
                binding.btnScenarioSequential.isEnabled = true
            }
        }

        setupUi()
    }

    private fun setupUi() {
        binding.btnScenario.setOnClickListener {
            playPointZero()
        }

        binding.btnScenarioSequential.setOnClickListener {
            playSequentialScenario()
        }

        binding.cvMediaPlayerButtons.apply {
            btnStop.setOnClickListener {
                if (audioEngine.isSoundPlaying(pointOneFnacSourceId)) audioEngine.stopSound(pointOneFnacSourceId)
                if (audioEngine.isSoundPlaying(pointOneWortenSourceId)) audioEngine.stopSound(pointOneWortenSourceId)
                if (audioEngine.isSoundPlaying(pointOneSpringfieldSourceId)) audioEngine.stopSound(pointOneSpringfieldSourceId)
                if (audioEngine.isSoundPlaying(pointOneZaraSourceId)) audioEngine.stopSound(pointOneZaraSourceId)

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

    private fun playSequentialScenario() {
        pointOneFnacSourceId = audioEngine.createSoundObject(POINT_ONE_FNAC)
        pointOneZaraSourceId = audioEngine.createSoundObject(POINT_ONE_ZARA)
        pointOneWortenSourceId = audioEngine.createSoundObject(POINT_ONE_WORTEN)
        pointOneSpringfieldSourceId = audioEngine.createSoundObject(POINT_ONE_SPRINGFIELD)

        audioEngine.setSoundObjectPosition(
            pointOneFnacSourceId,
            pointOneFnac[0],
            pointOneFnac[1],
            pointOneFnac[2]
        )

        audioEngine.setSoundObjectPosition(
            pointOneZaraSourceId,
            pointOneZara[0],
            pointOneZara[1],
            pointOneZara[2]
        )

        audioEngine.setSoundObjectPosition(
            pointOneWortenSourceId,
            pointOneWorten[0],
            pointOneWorten[1],
            pointOneWorten[2]
        )

        audioEngine.setSoundObjectPosition(
            pointOneSpringfieldSourceId,
            pointOneSpringfield[0],
            pointOneSpringfield[1],
            pointOneSpringfield[2]
        )

        scope.launch {
            audioEngine.playSound(pointOneFnacSourceId, false)

            delay(3000)

            audioEngine.playSound(pointOneZaraSourceId, false)

            delay(3000)

            audioEngine.playSound(pointOneSpringfieldSourceId, false)

            delay(3000)

            audioEngine.playSound(pointOneWortenSourceId, false)
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

        private const val POINT_ONE_FNAC = "fnac_10m.mp3"
        private const val POINT_ONE_ZARA = "zara_50m.mp3"
        private const val POINT_ONE_WORTEN = "worten_100m.mp3"
        private const val POINT_ONE_SPRINGFIELD = "springfield_50m.mp3"
    }
}