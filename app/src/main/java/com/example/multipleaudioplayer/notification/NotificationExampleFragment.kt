package com.example.multipleaudioplayer.notification

import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import com.amazonaws.auth.CognitoCachingCredentialsProvider
import com.amazonaws.regions.Regions
import com.amazonaws.services.polly.AmazonPollyPresigningClient
import com.amazonaws.services.polly.model.OutputFormat
import com.amazonaws.services.polly.model.SynthesizeSpeechPresignRequest
import com.amazonaws.services.polly.model.TextType
import com.example.multipleaudioplayer.NotificationHelper
import com.example.multipleaudioplayer.R
import com.example.multipleaudioplayer.databinding.LayoutNotificationExampleBinding
import com.example.multipleaudioplayer.utils.convertToSsml
import com.google.vr.sdk.audio.GvrAudioEngine
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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

    private var job: Job? = null

    private val documentSampleMediaPlayer: MediaPlayer by lazy {
        MediaPlayer.create(requireActivity(), R.raw.document_example)
    }

    private val notificationSampleMediaPlayer: MediaPlayer by lazy {
        MediaPlayer.create(requireActivity(), R.raw.notification_example)
    }

    private val audioEngine by lazy {
        GvrAudioEngine(requireActivity(), GvrAudioEngine.RenderingMode.BINAURAL_HIGH_QUALITY)
    }

    var clientNew: AmazonPollyPresigningClient? = null

    var mediaPlayer: MediaPlayer? = null

    var selectedVoice: String = "Cristiano"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initPollyClient()

        setupUi()

        setupNewMediaPlayer()
    }

    private fun setupUi() {
        // setup spinner (with portuguese voices)
        val voices = resources.getStringArray(R.array.Voices)
        val adapter = ArrayAdapter(requireContext(), R.layout.support_simple_spinner_dropdown_item, voices)
        binding.spinnerVoices.adapter = adapter

        binding.spinnerVoices.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
                selectedVoice = voices[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>) {

            }
        }

        binding.btnNotificationNoSpatialization.setOnClickListener {
            playVoice()
        }

        binding.btnNotificationSpatialization.setOnClickListener {
            playNotificationExampleWithSpatialization()
        }
    }

    private fun playNotificationExample() {
        job = scope.launch {
            //TODO
            //documentSampleMediaPlayer.start()

            // manually trigger notification after waiting 7 seconds
            // and play the notification audio
            delay(7000)

            launchNotification()
            notificationSampleMediaPlayer.start()
        }
    }

    private fun playNotificationExampleWithSpatialization() {
        binding.btnNotificationSpatialization.isEnabled = false
        binding.btnNotificationNoSpatialization.isEnabled = false
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
            audioEngine.playSound(documentSourceId, true /* looped playback */)

            // Preload an unspatialized sound to be played on a successful trigger on the cube.
            //audioEngine.preloadSoundFile(SUCCESS_SOUND_FILE)

            audioEngine.setSoundObjectPosition(
                notificationSourceId,
                notificationPosition[0],
                notificationPosition[1],
                notificationPosition[2]
            )

            delay(7000)
            launchNotification()
            audioEngine.playSound(notificationSourceId, false /* looped playback */)

            withContext(Dispatchers.Main) {
                binding.btnNotificationSpatialization.isEnabled = true
                binding.btnNotificationNoSpatialization.isEnabled = true
            }
        }
    }

    private fun launchNotification() {
        scope.launch {
            NotificationHelper.createSampleDataNotification(
                requireActivity(),
                getString(R.string.sample_data_loaded_title),
                getString(R.string.sample_data_loaded_message),
                getString(R.string.sample_data_loaded_big_text), false
            )
        }
    }

    private fun initPollyClient() {
        val credentialsProvider = CognitoCachingCredentialsProvider(
            requireContext(),
            "ap-northeast-2:c903f1b4-7a90-42ff-a53a-df75f34036b2",
            Regions.AP_NORTHEAST_2
        )
        clientNew = AmazonPollyPresigningClient(credentialsProvider)

        /*AWSMobileClient.getInstance().initialize(requireContext(), object : Callback<UserStateDetails?> {
            override fun onResult(result: UserStateDetails?) {
                // Create a client that supports generation of presigned URLs.
                client = AmazonPollyPresigningClient(AWSMobileClient.getInstance())
                Log.i("INIT", "onResult: " + result?.userState);
            }

            override fun onError(e: Exception) {
                Log.e(TAG, "onError: Initialization error", e)
            }
        })*/
    }

    private fun playVoice() {

        try {
            binding.btnNotificationNoSpatialization.isEnabled = false
            binding.btnNotificationSpatialization.isEnabled = false
            scope.launch {
                //TODO this might need to be changed (the formulas +50)
                val speedRate = binding.sliderSpeechRate.value + 50
                val pitch = binding.sliderSpeechPitch.value - 20
                val timbre = binding.sliderSpeechTimbre.value + 50

                // Create speech synthesis request.
                val synthesizeSpeechPresignRequest =
                    SynthesizeSpeechPresignRequest() // Set text to synthesize.
                        .withText(
                            getString(R.string.sample_text).convertToSsml(
                                speedRate = speedRate,
                                pitch = pitch,
                                timbre = timbre.toInt()
                            )
                        ) // Set voice selected by the user.
                        .withVoiceId(selectedVoice) // Set format to MP3.
                        .withTextType(TextType.Ssml) // Set format to ssml (to configure audio properties)
                        .withOutputFormat(OutputFormat.Mp3)

                // Get the presigned URL for synthesized speech audio stream.

                // Get the presigned URL for synthesized speech audio stream.
                val presignedSynthesizeSpeechUrl =
                    clientNew?.getPresignedSynthesizeSpeechUrl(synthesizeSpeechPresignRequest)


                // Set media player's data source to previously obtained URL.

                // Create a media player to play the synthesized audio stream.
                if (mediaPlayer?.isPlaying == true) {
                    setupNewMediaPlayer()
                }
                mediaPlayer?.setDataSource(presignedSynthesizeSpeechUrl.toString())
                mediaPlayer?.prepareAsync()
            }

        } catch (exception: java.lang.Exception) {
            Log.e(TAG, exception.toString())
        }
    }

    private fun setupNewMediaPlayer() {
        mediaPlayer = MediaPlayer()
        mediaPlayer?.setOnCompletionListener { mp ->
            mp.release()
            setupNewMediaPlayer()

            binding.btnNotificationSpatialization.isEnabled = true
            binding.btnNotificationNoSpatialization.isEnabled = true
        }
        mediaPlayer?.setOnPreparedListener { mp ->
            mp.start()
            playNotificationExample()
        }
        mediaPlayer?.setOnErrorListener { _, _, _ ->
            binding.btnNotificationSpatialization.isEnabled = true
            binding.btnNotificationNoSpatialization.isEnabled = true
            false
        }
    }

    override fun onPause() {
        audioEngine.pause() // to play sound in the background we just don't have to pause it
        mediaPlayer?.stop()
        mediaPlayer?.release()
        documentSampleMediaPlayer.stop()
        documentSampleMediaPlayer.release()
        job?.cancel()
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        audioEngine.resume()
    }

    companion object {
        private const val DOCUMENT_SOUND_FILE = "document_example.mp3"
        private const val NOTIFICATION_SOUND_FILE = "notification_example.mp3"
        private const val TAG = "Amazon Polly"
    }
}