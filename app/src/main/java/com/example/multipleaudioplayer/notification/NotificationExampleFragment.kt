package com.example.multipleaudioplayer.notification

import android.media.MediaPlayer
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.multipleaudioplayer.NotificationHelper
import com.example.multipleaudioplayer.R
import com.example.multipleaudioplayer.databinding.LayoutNotificationExampleBinding
import com.google.vr.sdk.audio.GvrAudioEngine
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class NotificationExampleFragment : Fragment(R.layout.layout_notification_example) {

    private val binding by viewBinding(LayoutNotificationExampleBinding::bind)

    private var documentSourceId = GvrAudioEngine.INVALID_ID
    private var notificationSourceId = GvrAudioEngine.INVALID_ID

    // position of the spatial sounds
    //TODO will need to change based on user configuration (maybe=?)

    // sound played on the left ear
    private var modelPosition = floatArrayOf(-8f, 0.0f, 0.0f)

    // sound played on the right ear
    private var notificationPosition = floatArrayOf(8f, 0.0f, 0.0f)

    private val scope = CoroutineScope(Dispatchers.IO)

    private val documentSampleMediaPlayer: MediaPlayer by lazy {
        MediaPlayer.create(requireActivity(), R.raw.document_example)
    }

    private val notificationSampleMediaPlayer: MediaPlayer by lazy {
        MediaPlayer.create(requireActivity(), R.raw.notification_example)
    }

    private val audioEngine by lazy {
        GvrAudioEngine(requireActivity(), GvrAudioEngine.RenderingMode.BINAURAL_HIGH_QUALITY)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUi()

        scope.launch {
            delay(7000)

            NotificationHelper.createSampleDataNotification(
                requireActivity(),
                getString(R.string.sample_data_loaded_title),
                getString(R.string.sample_data_loaded_message),
                getString(R.string.sample_data_loaded_big_text), false
            )
        }
    }

    private fun setupUi() {
        binding.btnNotificationNoSpatialization.setOnClickListener {
            playNotificationExample()
        }

        binding.btnNotificationSpatialization.setOnClickListener {
            playNotificationExampleWithSpatialization()
        }
    }

    private fun playNotificationExample() {
        scope.launch {
/*            documentSampleMediaPlayer.start()

            // manually trigger notification after waiting 7 seconds
            // and play the notification audio
            delay(7000)*/

            NotificationHelper.createSampleDataNotification(
                requireActivity(),
                getString(R.string.sample_data_loaded_title),
                getString(R.string.sample_data_loaded_message),
                getString(R.string.sample_data_loaded_big_text), false
            )

//            notificationSampleMediaPlayer.start()
        }
    }

    private fun playNotificationExampleWithSpatialization() {
        scope.launch {
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
            audioEngine.playSound(documentSourceId, true /* looped playback */)

            // Preload an unspatialized sound to be played on a successful trigger on the cube.
            //audioEngine.preloadSoundFile(SUCCESS_SOUND_FILE)

            audioEngine.setSoundObjectPosition(
                notificationSourceId,
                notificationPosition[0],
                notificationPosition[1],
                notificationPosition[2]
            )
            audioEngine.playSound(notificationSourceId, true /* looped playback */)
        }
    }

    /**
     * @param soundFile is the name of the sound file placed in the assets folder
     */
    private fun playSoundInPosition(
        position: FloatArray,
        soundFile: String,
        shouldBePlayedInLoop: Boolean = true
    ) {
        audioEngine.preloadSoundFile(soundFile)
        val sourceId = audioEngine.createSoundObject(soundFile)
        audioEngine.setSoundObjectPosition(sourceId, position[0], position[1], position[2])
        audioEngine.playSound(sourceId, shouldBePlayedInLoop)
    }

    override fun onPause() {
        audioEngine.pause() // to play sound in the background we just don't have to pause it
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        audioEngine.resume()
    }

    companion object {
        private const val DOCUMENT_SOUND_FILE = "document_example.mp3"
        private const val NOTIFICATION_SOUND_FILE = "notification_example.mp3"
    }
}