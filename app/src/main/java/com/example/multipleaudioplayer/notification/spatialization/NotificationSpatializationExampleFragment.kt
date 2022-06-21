package com.example.multipleaudioplayer.notification.spatialization

import android.media.MediaPlayer
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

class NotificationSpatializationExampleFragment :
    Fragment(R.layout.layout_notification_spatialization_example) {

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

    private val sweepMediaPlayer: MediaPlayer by lazy {
        MediaPlayer.create(requireActivity(), R.raw.air_sweep)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        scope.launch {
            withContext(Dispatchers.Main) {
                toggleButtons(false)
            }
            audioEngine.preloadSoundFile(DOCUMENT_SOUND_FILE)
            audioEngine.preloadSoundFile(NOTIFICATION_SOUND_FILE)
            audioEngine.preloadSoundFile(DOCUMENT_SOUND_INES_FILE)
            audioEngine.preloadSoundFile(NOTIFICATION_SOUND_CRISTIANO_FILE)

            withContext(Dispatchers.Main) {
                toggleButtons(true)
            }
        }

        setupUi()
    }

    private fun setupUi() {
        binding.btnNotificationExample.setOnClickListener {
            playNotificationExampleWithSpatialization()
        }

        binding.btnNotificationExampleSecondScenario.setOnClickListener {
            playSpatializationSecondScenario()
        }

        binding.cvMediaPlayerButtons.apply {
            btnStop.setOnClickListener {
                if (audioEngine.isSoundPlaying(documentSourceId))
                    audioEngine.stopSound(documentSourceId)
                job?.cancel()
                toggleButtons(true)
            }
            btnPlay.setOnClickListener { }
            btnPause.setOnClickListener { }
        }
    }

    private fun playNotificationExampleWithSpatialization() {
        toggleButtons(false)

        job = scope.launch {
            // Start spatial audio playback of OBJECT_SOUND_FILE at the model position. The
            // returned sourceId handle is stored and allows for repositioning the sound object
            // whenever the cube position changes.
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
            sweepMediaPlayer.start()
            delay(1000)

            launchNotification()
            audioEngine.playSound(notificationSourceId, false)

            withContext(Dispatchers.Main) {
                toggleButtons(true)
            }
        }
    }

    private fun playSpatializationSecondScenario() {
        toggleButtons(false)

        job = scope.launch {
            // Start spatial audio playback of OBJECT_SOUND_FILE at the model position. The
            // returned sourceId handle is stored and allows for repositioning the sound object
            // whenever the cube position changes.
            documentSourceId = audioEngine.createSoundObject(DOCUMENT_SOUND_INES_FILE)
            notificationSourceId = audioEngine.createSoundObject(NOTIFICATION_SOUND_CRISTIANO_FILE)
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
            sweepMediaPlayer.start()
            delay(1000)

            launchNotification()
            audioEngine.playSound(notificationSourceId, false)

            withContext(Dispatchers.Main) {
                toggleButtons(true)
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

    private fun toggleButtons(isEnabled: Boolean) {
        binding.btnNotificationExample.isEnabled = isEnabled
        binding.btnNotificationExampleSecondScenario.isEnabled = isEnabled
    }

    override fun onPause() {
        if (audioEngine.isSoundPlaying(documentSourceId))
            audioEngine.stopSound(documentSourceId)
        job?.cancel()
        super.onPause()
    }

    companion object {
        private const val DOCUMENT_SOUND_FILE = "notification_document_cristiano.mp3"
        private const val NOTIFICATION_SOUND_FILE = "notification_ines.mp3"
        private const val DOCUMENT_SOUND_INES_FILE = "notification_document_ines.mp3"
        private const val NOTIFICATION_SOUND_CRISTIANO_FILE = "notification_cristiano.mp3"
    }
}