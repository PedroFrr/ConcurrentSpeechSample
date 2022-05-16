package com.example.multipleaudioplayer.workers

import android.content.Context
import android.content.ContextWrapper
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.example.multipleaudioplayer.utils.AUDIO_FILE_PATH
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.util.*
import java.util.concurrent.CountDownLatch

class SynthesizeFileWorker(private val appContext: Context, workerParams: WorkerParameters) :
    Worker(appContext, workerParams) {

    private val scope = CoroutineScope(Dispatchers.IO)

    private lateinit var textToSpeechEngine: TextToSpeech

    private val utteranceId = UUID.randomUUID().toString()

    private val directory by lazy {
        val wrapper = ContextWrapper(appContext)
        wrapper.filesDir
    }

    private var audioFilePath: String? = null

    override fun doWork(): Result {
        val latch = CountDownLatch(1)

        scope.launch {
            setupTextToSpeech()

            textToSpeechEngine.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                override fun onDone(utteranceId: String?) {
                    Log.i("TAG", "onDone")
                    latch.countDown()
                }

                override fun onError(utteranceId: String?) {
                    Log.i("TAG", "onError")
                    latch.countDown()
                }

                override fun onStart(utteranceId: String?) {
                    Log.i("TAG", "onStart")
                }

            })
        }

        latch.await()

        val output = workDataOf(AUDIO_FILE_PATH to audioFilePath)
        return Result.success(output)
    }

/*    private fun setupTextToSpeechListener(){
        textToSpeechEngine.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onDone(utteranceId: String?) {
                Log.i("TAG", "onDone")

                val output
            }

            override fun onError(utteranceId: String?) {
                Log.i("TAG", "onError")
            }

            override fun onStart(utteranceId: String?) {
                Log.i("TAG", "onStart")
            }

        })
    }*/

    private suspend fun setupTextToSpeech() {
        textToSpeechEngine = TextToSpeech(appContext) { status ->
            if (status == TextToSpeech.SUCCESS) {
                val result = textToSpeechEngine.setLanguage(Locale.getDefault())
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    // error
                    Log.d("ScanningExampleFragment.TAG", "failed")
                } else {
                    scope.launch {
                        synthesizeToFile(
                            "Segundo exemplo do mixer"
                        )
                    }
                }
            }
        }
    }

    private suspend fun synthesizeToFile(text: String) =
        withContext(Dispatchers.IO) {
            val file = File(directory, "$utteranceId.wav")
            audioFilePath = file.path

            textToSpeechEngine.synthesizeToFile(text, null, file, utteranceId)
        }
}