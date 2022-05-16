package com.example.multipleaudioplayer.workers

import android.content.Context
import android.content.ContextWrapper
import android.net.Uri
import androidx.work.Worker
import androidx.work.WorkerParameters
import zeroonezero.android.audio_mixer.AudioMixer
import zeroonezero.android.audio_mixer.input.AudioInput
import zeroonezero.android.audio_mixer.input.GeneralAudioInput
import java.util.concurrent.CountDownLatch

class MixAudioWorker(private val appContext: Context, workerParams: WorkerParameters) :
    Worker(appContext, workerParams) {

    override fun doWork(): Result {
        val latch = CountDownLatch(1)

        val inputs = mutableListOf<AudioInput>()

        //val audioFilePath = inputData.getString(AUDIO_FILE_PATH) ?: return Result.failure()

        val paths = listOf(
            "/data/data/com.example.multipleaudioplayer/files/f1a46f65-d836-4dd6-b408-5488c61deba5.wav",
            "/data/data/com.example.multipleaudioplayer/files/2f49f527-02c9-4a24-9f17-ea9b0d0766e4.wav"
        )

        val wrapper = ContextWrapper(appContext)
        val directory = wrapper.filesDir

        val outputPath = "$directory/output_file.mp3"

        val audioMixer = AudioMixer(outputPath)

        val audioInputs = paths.map {
            val ai = GeneralAudioInput(appContext, Uri.parse(it), null)
            ai.startOffsetUs = 0
            return@map ai
        }

        //audioMixer.setSampleRate(44100);  // optional
        //audioMixer.setBitRate(128000); // optional
        //audioMixer.setChannelCount(2); // 1 or 2 // optional
        //audioMixer.setLoopingEnabled(true); // Only works for parallel mixing
        audioMixer.mixingType = AudioMixer.MixingType.PARALLEL

        audioInputs.forEach {
            audioMixer.addDataSource(it)
        }

        audioMixer.setProcessingListener(object : AudioMixer.ProcessingListener {
            override fun onProgress(progress: Double) {/*Do nothing*/
            }

            override fun onEnd() {
                latch.countDown()
            }

        })

        audioMixer.start()
        audioMixer.processAsync()

        latch.await()

        return Result.success()
    }

    companion object {
        const val Progress = "Progress"
        private const val delayDuration = 1L
    }

}
