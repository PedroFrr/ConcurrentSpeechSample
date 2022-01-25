package com.example.multipleaudioplayer

import android.media.MediaPlayer
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationManagerCompat
import com.example.multipleaudioplayer.databinding.ActivityMainBinding
import com.google.vr.sdk.audio.GvrAudioEngine
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {

    private val scope = CoroutineScope(Dispatchers.IO)

    private lateinit var binding: ActivityMainBinding

    private var mediaPlayer: MediaPlayer? = null

    private var mediaPlayer2: MediaPlayer? = null

    private val audioEngine by lazy {
        GvrAudioEngine(this, GvrAudioEngine.RenderingMode.BINAURAL_HIGH_QUALITY)
    }

    private var sourceId = GvrAudioEngine.INVALID_ID
    private var notificationSourceId = GvrAudioEngine.INVALID_ID

    private var successSourceId = GvrAudioEngine.INVALID_ID

    // sound played on the left ear
    private var modelPosition = floatArrayOf(-8f, 0.0f, 0.0f)

    // sound played on the right ear
    private var notificationPosition = floatArrayOf(8f, 0.0f, 0.0f)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        // Create channel notification for app
        NotificationHelper.createNotificationChannel(
            this,
            NotificationManagerCompat.IMPORTANCE_HIGH, false,
            getString(R.string.app_name), "App notification channel."
        )

        val normalNotificationButton = binding.normalNotificationButton

        normalNotificationButton.setOnClickListener {
            scope.launch {
                mediaPlayer = MediaPlayer.create(this@MainActivity, R.raw.document_example)
                mediaPlayer!!.start()

                delay(7000)

                NotificationHelper.createSampleDataNotification(
                    this@MainActivity,
                    getString(R.string.sample_data_loaded_title),
                    getString(R.string.sample_data_loaded_message),
                    getString(R.string.sample_data_loaded_big_text), false
                )

                mediaPlayer2 = MediaPlayer.create(this@MainActivity, R.raw.notification_example)
                mediaPlayer2!!.start()
            }
        }

        playSound()
    }

    private fun playSound(){
        // Start spatial audio playback of OBJECT_SOUND_FILE at the model position. The
        // returned sourceId handle is stored and allows for repositioning the sound object
        // whenever the cube position changes.

        // Start spatial audio playback of OBJECT_SOUND_FILE at the model position. The
        // returned sourceId handle is stored and allows for repositioning the sound object
        // whenever the cube position changes.
        audioEngine.preloadSoundFile(DOCUMENT_SOUND_FILE)
        audioEngine.preloadSoundFile(NOTIFICATION_SOUND_FILE)
        sourceId = audioEngine.createSoundObject(DOCUMENT_SOUND_FILE)
        notificationSourceId = audioEngine.createSoundObject(NOTIFICATION_SOUND_FILE)
        audioEngine.setSoundObjectPosition(sourceId, modelPosition[0], modelPosition[1], modelPosition[2])
        audioEngine.playSound(sourceId, true /* looped playback */)
        // Preload an unspatialized sound to be played on a successful trigger on the cube.
        //audioEngine.preloadSoundFile(SUCCESS_SOUND_FILE)


        audioEngine.setSoundObjectPosition(notificationSourceId, notificationPosition[0], notificationPosition[1], notificationPosition[2])
        audioEngine.playSound(notificationSourceId, true /* looped playback */)
    }

    override fun onPause() {
        audioEngine.pause()
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        audioEngine.resume()
    }

    companion object{
        private const val OBJECT_SOUND_FILE = "cube_sound.wav"
        private const val SUCCESS_SOUND_FILE = "success.wav"
        private const val DOCUMENT_SOUND_FILE = "document_example.mp3"
        private const val NOTIFICATION_SOUND_FILE = "notification_example.mp3"
    }
}