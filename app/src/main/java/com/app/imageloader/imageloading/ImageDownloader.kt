package com.app.imageloader.imageloading

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL
import javax.inject.Inject

class ImageDownloader @Inject constructor() {
    companion object {
        private const val DEFAULT_CONNECTION_TIMEOUT = 10000
        private const val DEFAULT_READ_TIMEOUT = 10000
    }

    suspend fun downloadImage(url: String?): Bitmap? {
        return withContext(Dispatchers.IO) {
            try {
                val connection = URL(url).openConnection() as HttpURLConnection
                connection.connectTimeout = DEFAULT_CONNECTION_TIMEOUT
                connection.readTimeout = DEFAULT_READ_TIMEOUT
                val inputStream = connection.inputStream
                BitmapFactory.decodeStream(inputStream)
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }
}