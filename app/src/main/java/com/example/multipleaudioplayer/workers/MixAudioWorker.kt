package com.example.multipleaudioplayer.workers

import android.content.Context
import android.content.ContextWrapper
import android.net.Uri
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.multipleaudioplayer.utils.AUDIO_FILE_PATH
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import zeroonezero.android.audio_mixer.AudioMixer
import zeroonezero.android.audio_mixer.input.AudioInput
import zeroonezero.android.audio_mixer.input.GeneralAudioInput

class MixAudioWorker(private val appContext: Context, workerParams: WorkerParameters) :
    Worker(appContext, workerParams) {

    override fun doWork(): Result{

        val inputs = mutableListOf<AudioInput>()

        //val audioFilePath = inputData.getString(AUDIO_FILE_PATH) ?: return Result.failure()

        val paths = listOf(
            "/data/data/com.example.multipleaudioplayer/files/241c8605-ac52-4863-926a-1b0b362b3abb.wav",
            "/data/data/com.example.multipleaudioplayer/files/262d3793-01e1-4c1d-a4d7-715bd8a94f9c.wav"
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

/*        audioMixer.setProcessingListener(object : AudioMixer.ProcessingListener{
            override fun onProgress(progress: Double) {
                //TODO
            }

            override fun onEnd() {
                //TODO
            }

        })*/

        audioMixer.start()
        audioMixer.processAsync()

        while(audioMixer.progress != 1.0){
            Log.i("TAG", "not processed")
        }

        return  Result.success()
    }

    companion object {
        const val Progress = "Progress"
        private const val delayDuration = 1L
    }

}
