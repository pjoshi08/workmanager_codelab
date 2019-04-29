package com.example.background.workers

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import androidx.work.Data
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.background.KEY_IMAGE_URI
import java.text.SimpleDateFormat
import java.util.*

class SaveImageToFileWorker(context: Context, workerParameters: WorkerParameters):
        Worker(context, workerParameters) {

    override fun doWork(): Result {
        val appContext = applicationContext
        val resolver = appContext.contentResolver

        try {
            val resourceUri = inputData.getString(KEY_IMAGE_URI)
            val picture = BitmapFactory.decodeStream(
                    resolver.openInputStream(Uri.parse(resourceUri)))

            val outputUri = MediaStore.Images.Media.insertImage(resolver, picture,
                    TITLE, DATE_FORMATTER.format(Date()))

            if (TextUtils.isEmpty(outputUri)) {
                Log.e(TAG, "doWork: Writing to MediaStore failed")
                return Result.failure()
            }

            val outputData = Data.Builder()
                    .putString(KEY_IMAGE_URI, outputUri.toString())
                    .build()

            // Manual slow for cancel work
            sleep()

            return Result.success(outputData)
        } catch (e: Exception) {
            Log.e(TAG, "doWork: Unable to save image to Gallery", e)
            return Result.failure()
        }
    }

    companion object {
        private val TAG = "SaveImageToFileWorker"
        private const val TITLE = "Blurred Image"
        @SuppressLint("ConstantLocale")
        private val DATE_FORMATTER = SimpleDateFormat("yyyy.MM.dd 'at' HH:mm:ss z", Locale.getDefault())
    }
}