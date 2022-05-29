package com.example.multipleaudioplayer.notification.spatialization

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.multipleaudioplayer.NotificationHelper
import com.example.multipleaudioplayer.R
import com.example.multipleaudioplayer.databinding.LayoutNotificationSpatializationExampleBinding
import com.google.vr.sdk.audio.GvrAudioEngine
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class NotificationSpatializationExampleFragment : Fragment(R.layout.layout_notification_spatialization_example) {

    private val binding by viewBinding(LayoutNotificationSpatializationExampleBinding::bind)

    private var documentSourceId = GvrAudioEngine.INVALID_ID
    private var notificationSourceId = GvrAudioEngine.INVALID_ID

    // sound played on the left ear
    private var modelPosition = floatArrayOf(-8f, 0.0f, 0.0f)

    // sound played on the right ear
    private var notificationPosition = floatArrayOf(8f, 0.0f, 0.0f)

    private val scope = CoroutineScope(Dispatchers.IO)

    private var job: Job? = null

    private val audioEngine by lazy {
        GvrAudioEngine(requireActivity(), GvrAudioEngine.RenderingMode.BINAURAL_HIGH_QUALITY)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUi()
    }

    private fun setupUi() {
        binding.btnNotificationExample.setOnClickListener {
            playNotificationExampleWithSpatialization()
        }
    }

    private fun playNotificationExampleWithSpatialization() {
        binding.btnNotificationExample.isEnabled = false

        job = scope.launch {
            // Start spatial audio playback of OBJECT_SOUND_FILE at the model position. The
            // returned sourceId handle is stored and allows for repositioning the sound object
            // whenever the cube position changes.
            audioEngine.preloadSoundFile(DOCUMENT_SOUND_FILE)
            audioEngine.preloadSoundFile(NOTIFICATION_SOUND_FILE)
            documentSourceId = audioEngine.createSoundObject(DOCUMENT_SOUND_FILE)
            notificationSourceId = audioEngine.createSoundObject(NOTIFICATION_SOUND_FILE)
            audioEngine.setSoundObjectPosition(
                documentSourceId,
                modelPosition[0],
                modelPosition[1],
                modelPosition[2]
            )
            audioEngine.playSound(documentSourceId, false)

            // Preload an unspatialized sound to be played on a successful trigger on the cube.
            //audioEngine.preloadSoundFile(SUCCESS_SOUND_FILE)

            audioEngine.setSoundObjectPosition(
                notificationSourceId,
                notificationPosition[0],
                notificationPosition[1],
                notificationPosition[2]
            )

            delay(10000)
            launchNotification()
            audioEngine.playSound(notificationSourceId, false)

            withContext(Dispatchers.Main) {
                binding.btnNotificationExample.isEnabled = true
            }
        }
    }

    private fun launchNotification() {
        scope.launch {
            NotificationHelper.createSampleDataNotification(
                requireActivity(),
                getString(R.string.notification_title),
                getString(R.string.notification_message),
                getString(R.string.notification_message), false
            )
        }
    }

    override fun onPause() {
        audioEngine.pause() // to play sound in the background we just don't have to pause it
        job?.cancel()
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        audioEngine.resume()
    }

    companion object {
        private const val DOCUMENT_SOUND_FILE = "first_notice_example.mp3"
        private const val NOTIFICATION_SOUND_FILE = "notification_ines.mp3"
    }
}