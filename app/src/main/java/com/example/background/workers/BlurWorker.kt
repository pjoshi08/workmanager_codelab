package com.example.background.workers

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.text.TextUtils
import android.util.Log
import androidx.work.Data
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.background.KEY_IMAGE_URI
import java.lang.IllegalArgumentException

class BlurWorker(context: Context,
                 workerParameters: WorkerParameters)
    : Worker(context, workerParameters) {
    private val TAG = "BlurWorker"

    override fun doWork(): Result {
        val appContext = applicationContext
        try {
            // Get Input Uri
            val resourceUri = inputData.getString(KEY_IMAGE_URI)

            if (TextUtils.isEmpty(resourceUri)) {
                Log.e(TAG, "doWork: Invalid input Uri")
                throw IllegalArgumentException("Invalid input Uri")
            }

            val resolver = appContext.contentResolver

            val picture = BitmapFactory.decodeStream(
                    resolver.openInputStream(Uri.parse(resourceUri)))

            // Blur the bitmap
            val output = blurBitmap(picture, appContext)

            // Write bitmap to a temp file
            val outputUri = writeBitmapToFile(appContext, output)

            makeStatusNotification("Output is $outputUri", appContext)

            // Manual slow for cancel work
            sleep()

            // Output Data
            val outputData = Data.Builder()
                    .putString(KEY_IMAGE_URI, outputUri.toString())
                    .build()

            // If there were no errors, return SUCCESS
            return Result.success(outputData)
        } catch (t: Throwable) {
            // Technically WorkManager will return Result.failure()
            // but it's best to be explicit about it.
            // Thus if there were errors, we're return FAILURE
            Log.e(TAG, "doWork: Error applying Blur", t)
            return Result.failure()
        }
    }
}