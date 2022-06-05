package com.example.multipleaudioplayer.notification.nospatialization

import android.media.MediaPlayer
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.amazonaws.auth.CognitoCachingCredentialsProvider
import com.amazonaws.regions.Regions
import com.amazonaws.services.polly.AmazonPollyPresigningClient
import com.amazonaws.services.polly.model.OutputFormat
import com.amazonaws.services.polly.model.SynthesizeSpeechPresignRequest
import com.amazonaws.services.polly.model.TextType
import com.example.multipleaudioplayer.NotificationHelper
import com.example.multipleaudioplayer.R
import com.example.multipleaudioplayer.databinding.LayoutNotificationNoSpatializationExampleBinding
import com.example.multipleaudioplayer.utils.convertToSsml
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class NotificationNoSpatializationExampleFragment : Fragment(R.layout.layout_notification_no_spatialization_example) {

    private val binding by viewBinding(LayoutNotificationNoSpatializationExampleBinding::bind)

    private val scope = CoroutineScope(Dispatchers.IO)

    private var job: Job? = null

    private var notificationSampleMediaPlayer: MediaPlayer? = null

    var clientNew: AmazonPollyPresigningClient? = null

    var mediaPlayer: MediaPlayer? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initPollyClient()

        setupUi()

        setupNewMediaPlayer()

        setupNewNotificationMediaPlayer()

        setupDefaultVoices()
    }

    private fun setupDefaultVoices() {
        binding.notificationProperties.spinnerVoices.setSelection(1)
    }

    private fun setupUi() {
        setupVoiceSpinners()

        val configurableAudioChannels = listOf("Áudio Principal", "Notificação")
        val configurableAudioChannelsAdapter = ArrayAdapter(
            requireContext(),
            R.layout.support_simple_spinner_dropdown_item,
            configurableAudioChannels
        )
        binding.spinnerAudioChannel.adapter = configurableAudioChannelsAdapter

        binding.spinnerAudioChannel.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View,
                    position: Int,
                    id: Long
                ) {
                    setAudioChannelPropertiesVisibility()
                }

                override fun onNothingSelected(parent: AdapterView<*>) {

                }
            }

        binding.btnNotificationNoSpatialization.setOnClickListener {
            playVoice()
        }

        binding.cvMediaPlayerButtons.apply {
            btnStop.setOnClickListener {
                if (mediaPlayer?.isPlaying == true) {
                    mediaPlayer?.stop()
                    mediaPlayer?.release()
                }

                if (notificationSampleMediaPlayer?.isPlaying == true) {
                    notificationSampleMediaPlayer?.stop()
                    notificationSampleMediaPlayer?.release()
                }

                job?.cancel()
                toggleButtons(true)
            }
            btnPlay.setOnClickListener { }
            btnPause.setOnClickListener { }
        }
    }

    private fun setupVoiceSpinners() {
        val voices = resources.getStringArray(R.array.Voices)
        val adapter =
            ArrayAdapter(requireContext(), R.layout.support_simple_spinner_dropdown_item, voices)
        binding.notificationProperties.spinnerVoices.adapter = adapter
        binding.mainDocumentProperties.spinnerVoices.adapter = adapter
    }

    private fun setAudioChannelPropertiesVisibility() {
        val selectedAudioChannel = binding.spinnerAudioChannel.selectedItem.toString()
        binding.mainDocumentProperties.audioChannelProperties.isVisible =
            selectedAudioChannel == "Áudio Principal"

        binding.notificationProperties.audioChannelProperties.isVisible =
            selectedAudioChannel == "Notificação"

    }

    private fun playNotificationExample() {
        job = scope.launch {
            // manually trigger notification after waiting 7 seconds
            // and play the notification audio
            delay(10000)

            launchNotification()
            notificationSampleMediaPlayer?.start()
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

    private fun initPollyClient() {
        val credentialsProvider = CognitoCachingCredentialsProvider(
            requireContext(),
            "ap-northeast-2:c903f1b4-7a90-42ff-a53a-df75f34036b2",
            Regions.AP_NORTHEAST_2
        )
        clientNew = AmazonPollyPresigningClient(credentialsProvider)
    }

    private fun playVoice() {

        binding.btnNotificationNoSpatialization.isEnabled = false
        scope.launch {
            awaitAll(
                async { setupMainDocumentMediaPlayer() },
                async { setupNotificationMediaPlayer() }
            )
        }
    }

    private fun setupMainDocumentMediaPlayer() {
        //TODO this might need to be changed (the formulas +50)
        val speedRate = binding.mainDocumentProperties.sliderSpeechRate.value + 50
        val pitch = binding.mainDocumentProperties.sliderSpeechPitch.value - 20
        val timbre = binding.mainDocumentProperties.sliderSpeechTimbre.value + 50

        val selectedVoice = binding.mainDocumentProperties.spinnerVoices.selectedItem.toString()

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
        setupNewMediaPlayer()
        mediaPlayer?.setDataSource(presignedSynthesizeSpeechUrl.toString())
        mediaPlayer?.prepareAsync()
    }

    private fun setupNotificationMediaPlayer() {
        //TODO this might need to be changed (the formulas +50)
        val speedRate = binding.notificationProperties.sliderSpeechRate.value + 50
        val pitch = binding.notificationProperties.sliderSpeechPitch.value - 20
        val timbre = binding.notificationProperties.sliderSpeechTimbre.value + 50

        val selectedVoice = binding.notificationProperties.spinnerVoices.selectedItem.toString()

        // Create speech synthesis request.
        val synthesizeSpeechPresignRequest =
            SynthesizeSpeechPresignRequest() // Set text to synthesize.
                .withText(
                    getString(R.string.notification_message).convertToSsml(
                        speedRate = speedRate,
                        pitch = pitch,
                        timbre = timbre.toInt()
                    )
                ) // Set voice selected by the user.
                .withVoiceId(selectedVoice) // Set format to MP3.
                .withTextType(TextType.Ssml) // Set format to ssml (to configure audio properties)
                .withOutputFormat(OutputFormat.Mp3)

        // Get the presigned URL for synthesized speech audio stream.
        val presignedSynthesizeSpeechUrl =
            clientNew?.getPresignedSynthesizeSpeechUrl(synthesizeSpeechPresignRequest)


        // Set media player's data source to previously obtained URL.

        // Create a media player to play the synthesized audio stream.
        setupNewNotificationMediaPlayer()
        notificationSampleMediaPlayer?.setDataSource(presignedSynthesizeSpeechUrl.toString())
        notificationSampleMediaPlayer?.prepareAsync()
    }

    private fun setupNewMediaPlayer() {
        mediaPlayer = MediaPlayer()
        mediaPlayer?.setOnCompletionListener { mp ->
            mp.release()
            setupNewMediaPlayer()

            binding.btnNotificationNoSpatialization.isEnabled = true
        }
        mediaPlayer?.setOnPreparedListener { mp ->
            mp.start()
        }
        mediaPlayer?.setOnErrorListener { _, _, _ ->
            binding.btnNotificationNoSpatialization.isEnabled = true
            false
        }
    }

    private fun setupNewNotificationMediaPlayer() {
        notificationSampleMediaPlayer = MediaPlayer()
        notificationSampleMediaPlayer?.setOnCompletionListener { mp ->
            mp.release()
            setupNewNotificationMediaPlayer()
        }
        notificationSampleMediaPlayer?.setOnPreparedListener {
            playNotificationExample()
        }
    }

    private fun toggleButtons(isEnabled: Boolean) {
        binding.btnNotificationNoSpatialization.isEnabled = isEnabled
    }

    override fun onPause() {
        if (mediaPlayer?.isPlaying == true) {
            mediaPlayer?.stop()
            mediaPlayer?.release()
        }

        if (notificationSampleMediaPlayer?.isPlaying == true) {
            notificationSampleMediaPlayer?.stop()
            notificationSampleMediaPlayer?.release()
        }

        job?.cancel()
        super.onPause()
    }
}