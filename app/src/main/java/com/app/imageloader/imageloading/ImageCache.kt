package com.app.imageloader.imageloading

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.LruCache
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ImageCache @Inject constructor(private val context: Context) {

    companion object {
        private const val IMAGE_CACHE_DIRECTORY = "image_cache"
    }

    private val memoryCache: LruCache<String, Bitmap> = createMemoryCache()
    private val diskCacheDir: File = createDiskCacheDir()

    private fun createMemoryCache(): LruCache<String, Bitmap> {
        val maxMemory = (Runtime.getRuntime().maxMemory() / 1024).toInt()
        val cacheSize = maxMemory / 8
        return object : LruCache<String, Bitmap>(cacheSize) {
            override fun sizeOf(key: String, value: Bitmap): Int {
                return value.byteCount / 1024
            }
        }
    }

    private fun createDiskCacheDir(): File {
        val diskCacheDir = File(context.cacheDir, IMAGE_CACHE_DIRECTORY)
        if (!diskCacheDir.exists()) {
            diskCacheDir.mkdirs()
        }
        return diskCacheDir
    }

    suspend fun getBitmap(url: String): Bitmap? {
        //read from memory
        val bitmapFromMemory = memoryCache.get(url)
        if (bitmapFromMemory != null) {
            return bitmapFromMemory
        }

        //read from disk cache
        return loadBitmapFromDiskCache(url)
    }


    private suspend fun loadBitmapFromDiskCache(url: String): Bitmap? =
        withContext(Dispatchers.IO) {
            val file = File(diskCacheDir, getFileNameFromUrl(url))
            if (file.exists()) {
                return@withContext try {
                    val bitmap = BitmapFactory.decodeFile(file.absolutePath)
                    if (bitmap != null)
                        memoryCache.put(url, bitmap)
                    bitmap
                } catch (e: Exception) {
                    e.printStackTrace()
                    null
                }
            }
            return@withContext null
        }


    suspend fun putBitmap(url: String, bitmap: Bitmap) {
        memoryCache.put(url, bitmap)
        saveToDiskCache(url, bitmap)
    }

    private suspend fun saveToDiskCache(url: String, bitmap: Bitmap) {
        withContext(Dispatchers.IO) {
            val file = File(diskCacheDir, getFileNameFromUrl(url))
            try {
                FileOutputStream(file).use { out ->
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
                }
            } catch (e: IOException) {
                file.delete()
                e.printStackTrace()
            }
        }
    }

    private fun getFileNameFromUrl(url: String): String {
        val fileName = url.hashCode().toString()
        val fileExtension = url.substringAfterLast('.', "")
        return "$fileName.$fileExtension"
    }
}