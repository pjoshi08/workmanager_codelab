package com.example.background.workers

import android.content.Context
import android.text.TextUtils
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.background.OUTPUT_PATH
import java.io.File

class CleanupWorker(context: Context, workerParameters: WorkerParameters):
        Worker(context, workerParameters) {
    private val TAG = "CleanupWorker"

    override fun doWork(): Result {
        val appContext = applicationContext

        try {
            val outputDirectory = File(appContext.filesDir, OUTPUT_PATH)

            if (outputDirectory.exists()) {
                val entries = outputDirectory.listFiles()
                if (entries != null && entries.isNotEmpty()) {
                    for (entry in entries) {
                        val fileName = entry.name
                        if (!TextUtils.isEmpty(fileName) && fileName.endsWith(".png")) {
                            val deleted = entry.delete()
                            Log.i(TAG, "Deleted $fileName - $deleted")
                        }
                    }
                }
            }

            // Manual slow for cancel work
            sleep()

            return Result.success()
        } catch (e: Exception) {
            Log.e(TAG, "doWork: Error cleaning up", e)
            return Result.failure()
        }
    }
}