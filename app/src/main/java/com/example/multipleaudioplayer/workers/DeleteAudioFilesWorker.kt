package com.example.multipleaudioplayer.workers

import android.content.Context
import android.content.ContextWrapper
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DeleteAudioFilesWorker(private val appContext: Context, workerParams: WorkerParameters) :
    CoroutineWorker(appContext, workerParams) {

    private val directory by lazy {
        val wrapper = ContextWrapper(appContext)
        wrapper.filesDir
    }

    override suspend fun doWork(): Result {
        return withContext(Dispatchers.IO) {
            try {
                cleanupDirectory()
                Result.success()
            } catch (exception: Exception) {
                Log.e(TAG, "Error cleaning up", exception)
                Result.failure()
            }
        }
    }

    /** Removes pngs from the app's files directory */
    private fun cleanupDirectory() {
        directory.apply {
            if (exists()) {
                listFiles()?.forEach { file ->
                    if (file.name.endsWith(".png")) {
                        val deleted = file.delete()
                        Log.i(TAG, "Deleted ${file.name} - $deleted")
                    }
                }
            }
        }
    }

    companion object {
        private const val TAG = "FILE"
    }
}