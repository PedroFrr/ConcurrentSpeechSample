package com.example.multipleaudioplayer.spatialization

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.multipleaudioplayer.R
import com.example.multipleaudioplayer.databinding.LayoutSpatializationExampleBinding
import com.example.multipleaudioplayer.notification.NotificationExampleFragment
import com.google.vr.sdk.audio.GvrAudioEngine
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

class SpatializationExampleFragment : Fragment(R.layout.layout_spatialization_example) {

    private val binding by viewBinding(LayoutSpatializationExampleBinding::bind)

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

        playSpatialization()
    }

    private fun setupUi() {

    }

    private fun playSpatialization(){
        audioEngine.preloadSoundFile(DOCUMENT_PART_ONE_SOUND_FILE)
        audioEngine.preloadSoundFile(DOCUMENT_PART_TWO_SOUND_FILE)
        documentPartOneSourceId = audioEngine.createSoundObject(DOCUMENT_PART_ONE_SOUND_FILE)
        documentPartTwoSourceId = audioEngine.createSoundObject(DOCUMENT_PART_TWO_SOUND_FILE)

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

    override fun onPause() {
        audioEngine.pause()
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        audioEngine.resume()
    }

    companion object {
        private const val DOCUMENT_PART_ONE_SOUND_FILE = "part1.mp3"
        private const val DOCUMENT_PART_TWO_SOUND_FILE = "part2.mp3"
    }
}