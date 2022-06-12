package com.example

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.annotation.AnyRes
import androidx.fragment.app.Fragment
import com.example.multipleaudioplayer.R
import com.example.multipleaudioplayer.databinding.ExampleFragBinding
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.util.MimeTypes
import com.google.android.exoplayer2.util.Util
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding

class SpatialAudioExampleFragment : Fragment(R.layout.example_frag) {

    private val binding by viewBinding(ExampleFragBinding::bind)

    private var player: ExoPlayer? = null
    private var playWhenReady = true
    private var currentItem = 0
    private var startPosition = 0L


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUi()
    }

    private fun setupUi() {
        binding.btnPlay.setOnClickListener {
            if (player == null) initializePlayer()
            player?.play()
        }

        binding.btnStop.setOnClickListener {
            releasePlayer()
        }
    }

    private fun initializePlayer() {
        player = ExoPlayer.Builder(requireContext())
            .build()
            .also { exoPlayer ->
                val uri = requireContext().getResourceUri(R.raw.spatial_with_three)
                val mediaItem = MediaItem.Builder()
                    .setUri(uri)
                    .setMimeType(MimeTypes.AUDIO_WAV)
                    .build()

                exoPlayer.setMediaItem(mediaItem)
                //exoPlayer.playWhenReady = playWhenReady
                exoPlayer.seekTo(currentItem, startPosition)
                exoPlayer.addListener(playbackStateListener())

                exoPlayer.prepare()
            }
    }

    private fun playbackStateListener() = object : Player.Listener {
        override fun onPlaybackStateChanged(playbackState: Int) {
            when (playbackState) {
                ExoPlayer.STATE_IDLE -> "ExoPlayer.STATE_IDLE      -"
                ExoPlayer.STATE_BUFFERING -> "ExoPlayer.STATE_BUFFERING -"
                ExoPlayer.STATE_READY -> "ExoPlayer.STATE_READY     -"
                ExoPlayer.STATE_ENDED -> initializePlayer()
                else -> "UNKNOWN_STATE             -"
            }
        }
    }


    override fun onStart() {
        super.onStart()
        if (Util.SDK_INT > 23) {
            initializePlayer()
        }
    }

    override fun onResume() {
        super.onResume()
        //hideSystemUi()
        if (Util.SDK_INT <= 23 || player == null) {
            initializePlayer()
        }
    }

    override fun onPause() {
        super.onPause()
        if (Util.SDK_INT <= 23) {
            releasePlayer()
        }
    }

    override fun onStop() {
        super.onStop()
        if (Util.SDK_INT > 23) {
            releasePlayer()
        }
    }

    private fun releasePlayer() {
        player?.let { exoPlayer ->
            currentItem = exoPlayer.currentMediaItemIndex
            playWhenReady = exoPlayer.playWhenReady
            exoPlayer.removeListener(playbackStateListener())
            exoPlayer.release()
        }
        player = null
    }
}

internal fun Context.getResourceUri(@AnyRes resourceId: Int): Uri =
    Uri.Builder()
        .scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
        .authority(packageName)
        .path(resourceId.toString())
        .build()