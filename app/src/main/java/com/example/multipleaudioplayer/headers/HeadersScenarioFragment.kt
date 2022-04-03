package com.example.multipleaudioplayer.headers

import android.media.MediaPlayer
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.amazonaws.services.polly.AmazonPollyPresigningClient
import com.amazonaws.services.polly.model.OutputFormat
import com.amazonaws.services.polly.model.SynthesizeSpeechPresignRequest
import com.amazonaws.services.polly.model.VoiceId
import com.example.multipleaudioplayer.R
import com.example.multipleaudioplayer.databinding.LayoutParagraphHeadersBinding
import com.example.multipleaudioplayer.gestures.ItemsListAdapter
import com.google.vr.sdk.audio.GvrAudioEngine
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URL

class HeadersScenarioFragment : Fragment(R.layout.layout_paragraph_headers) {

    private val binding by viewBinding(LayoutParagraphHeadersBinding::bind)

    private val audioEngine by lazy {
        GvrAudioEngine(requireActivity(), GvrAudioEngine.RenderingMode.BINAURAL_HIGH_QUALITY)
    }

    private val listAdapter by lazy { ItemsListAdapter() }

    var clientNew: AmazonPollyPresigningClient? = null

    private val scenarioMediaPlayer1: MediaPlayer by lazy {
        MediaPlayer.create(requireActivity(), R.raw.facebook)
    }
    private val scenarioMediaPlayer2: MediaPlayer by lazy {
        MediaPlayer.create(requireActivity(), R.raw.travel)
    }
    private val scenarioMediaPlayer3: MediaPlayer by lazy {
        MediaPlayer.create(requireActivity(), R.raw.instagram)
    }
    private val scenarioMediaPlayer4: MediaPlayer by lazy {
        MediaPlayer.create(requireActivity(), R.raw.adventure)
    }
    private val scenarioMediaPlayer5: MediaPlayer by lazy {
        MediaPlayer.create(requireActivity(), R.raw.tripadvisor)
    }
    private val scenarioMediaPlayer6: MediaPlayer by lazy {
        MediaPlayer.create(requireActivity(), R.raw.candycrush)
    }
    private val scenarioMediaPlayer7: MediaPlayer by lazy {
        MediaPlayer.create(requireActivity(), R.raw.football)
    }
    private val scenarioMediaPlayer8: MediaPlayer by lazy {
        MediaPlayer.create(requireActivity(), R.raw.basketball)
    }
    private val scenarioMediaPlayer9: MediaPlayer by lazy {
        MediaPlayer.create(requireActivity(), R.raw.hockey)
    }

    private var source1 = GvrAudioEngine.INVALID_ID
    private var source2 = GvrAudioEngine.INVALID_ID
    private var source3 = GvrAudioEngine.INVALID_ID
    private var source4 = GvrAudioEngine.INVALID_ID
    private var source5 = GvrAudioEngine.INVALID_ID
    private var source6 = GvrAudioEngine.INVALID_ID
    private var source7 = GvrAudioEngine.INVALID_ID
    private var source8 = GvrAudioEngine.INVALID_ID
    private var source9 = GvrAudioEngine.INVALID_ID

    private val scope = CoroutineScope(Dispatchers.IO)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUi()
    }

    private fun setupUi() {


        setupListAdapter()

        listAdapter.submitList(items)

        binding.btnButtonsScenario.setOnClickListener {
            scope.launch {
                awaitAll(
                    async { scenarioMediaPlayer1.start() },
                    async { scenarioMediaPlayer2.start() },
                    async { scenarioMediaPlayer3.start() },
                )
                delay(2000)

                awaitAll(
                    async { scenarioMediaPlayer4.start() },
                    async { scenarioMediaPlayer5.start() },
                    async { scenarioMediaPlayer6.start() },
                )
                delay(2000)

                awaitAll(
                    async { scenarioMediaPlayer7.start() },
                    async { scenarioMediaPlayer8.start() },
                    async { scenarioMediaPlayer9.start() },
                )
            }
        }

        binding.btnButtonsSpalization.setOnClickListener {
            binding.btnButtonsSpalization.isEnabled = false
            binding.btnButtonsScenario.isEnabled = false

            scope.launch {
                audioEngine.preloadSoundFile(FACEBOOK)
                audioEngine.preloadSoundFile(TRAVEL)
                audioEngine.preloadSoundFile(INSTAGRAM)
                audioEngine.preloadSoundFile(ADVENTuRE)
                audioEngine.preloadSoundFile(TRIPADVISOR)
                audioEngine.preloadSoundFile(CANDYCRUSH)
                audioEngine.preloadSoundFile(FOOTBALL)
                audioEngine.preloadSoundFile(BASKETBALL)
                audioEngine.preloadSoundFile(HOCKEY)


                source1 = audioEngine.createSoundObject(FACEBOOK)
                source2 = audioEngine.createSoundObject(TRAVEL)
                source3 = audioEngine.createSoundObject(INSTAGRAM)
                source4 = audioEngine.createSoundObject(ADVENTuRE)
                source5 = audioEngine.createSoundObject(TRIPADVISOR)
                source6 = audioEngine.createSoundObject(CANDYCRUSH)
                source7 = audioEngine.createSoundObject(FOOTBALL)
                source8 = audioEngine.createSoundObject(BASKETBALL)
                source9 = audioEngine.createSoundObject(HOCKEY)

                audioEngine.setSoundObjectPosition(source1, -8.0f, 0.0f, 0.0f)
                audioEngine.setSoundObjectPosition(source2, 8.0f, 0.0f, 0.0f)
                audioEngine.setSoundObjectPosition(source3, -8.0f, 0.0f, 0.0f)

                audioEngine.setSoundObjectPosition(source4, -8.0f, 0.0f, 0.0f)
                audioEngine.setSoundObjectPosition(source5, 8.0f, 0.0f, 0.0f)
                audioEngine.setSoundObjectPosition(source6, -8.0f, 0.0f, 0.0f)

                audioEngine.setSoundObjectPosition(source7, -8.0f, 0.0f, 0.0f)
                audioEngine.setSoundObjectPosition(source8, 8.0f, 0.0f, 0.0f)
                audioEngine.setSoundObjectPosition(source9, -8.0f, 0.0f, 0.0f)

                awaitAll(
                    async { audioEngine.playSound(source1, false) },
                    async { audioEngine.playSound(source2, false) },
                    async { audioEngine.playSound(source3, false) },
                )
                delay(2000)

                awaitAll(
                    async { audioEngine.playSound(source4, false) },
                    async { audioEngine.playSound(source5, false) },
                    async { audioEngine.playSound(source6, false) },
                )
                delay(2000)

                awaitAll(
                    async { audioEngine.playSound(source7, false) },
                    async { audioEngine.playSound(source8, false) },
                    async { audioEngine.playSound(source9, false) },
                )

                withContext(Dispatchers.Main) {
                    binding.btnButtonsScenario.isEnabled = true
                    binding.btnButtonsSpalization.isEnabled = true
                }
            }
        }
    }

    private fun getAudioFromText(text: String): URL? {
        // Create speech synthesis request.
        val synthesizeSpeechPresignRequest =
            SynthesizeSpeechPresignRequest() // Set text to synthesize.
                .withText(text) // Set voice selected by the user.
                .withVoiceId(VoiceId.Emma) // Set format to MP3.
                .withOutputFormat(OutputFormat.Mp3)

        // Get the presigned URL for synthesized speech audio stream.
        return clientNew?.getPresignedSynthesizeSpeechUrl(synthesizeSpeechPresignRequest)
    }

    private fun setupListAdapter() {
        binding.rvHomescreen.apply {
            adapter = listAdapter
            layoutManager = GridLayoutManager(requireContext(), 3)
            hasFixedSize()
        }
    }

    override fun onPause() {
        audioEngine.pause()

        super.onPause()
    }

    companion object {
        val items = listOf(
            "Facebook",
            "Travel",
            "Instagram",
            "Adventure",
            "TripAdvisor",
            "Candy crush",
            "Football",
            "Basketball",
            "Hockey"
        )

        private const val FACEBOOK = "facebook.mp3"
        private const val TRAVEL = "travel.mp3"
        private const val INSTAGRAM = "instagram.mp3"
        private const val ADVENTuRE = "adventure.mp3"
        private const val TRIPADVISOR = "tripadvisor.mp3"
        private const val CANDYCRUSH = "candycrush.mp3"
        private const val FOOTBALL = "football.mp3"
        private const val BASKETBALL = "basketball.mp3"
        private const val HOCKEY = "hockey.mp3"
    }
}