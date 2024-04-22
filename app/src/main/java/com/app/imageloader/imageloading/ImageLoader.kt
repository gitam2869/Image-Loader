package com.app.imageloader.imageloading

import android.widget.ImageView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

const val TAG = "ImageLoader"

class ImageLoader @Inject constructor(
    private val imageDownloader: ImageDownloader,
    private val imageCache: ImageCache
) {
    private var loadingJobs: MutableMap<ImageView, Job?> = mutableMapOf()
    private var downloadingJobs: MutableMap<String, Job?> = mutableMapOf()

    fun loadImage(
        url: String?,
        placeHolder: Int? = null,
        errorPlaceHolder: Int? = null,
        imageView: ImageView
    ) {
        placeHolder?.let {
            imageView.setImageResource(it)
        }

        loadingJobs[imageView]?.cancel()

        url?.let {
            val job = CoroutineScope(Dispatchers.Main).launch {
                val bitmapFromCache = imageCache.getBitmap(it)
                if (bitmapFromCache != null) {
                    imageView.setImageBitmap(bitmapFromCache)
                } else {
                    val downloadJob = launch {
                        val bitmap = imageDownloader.downloadImage(url)
                        if (bitmap != null) {
                            val downscaledBitmap = BitmapUtils.getDownscaledBitmap(bitmap, imageView)
                            imageView.setImageBitmap(downscaledBitmap)
                            imageCache.putBitmap(url, downscaledBitmap)
                        } else {
                            errorPlaceHolder?.let { imageView.setImageResource(it) }
                        }
                    }
                    downloadingJobs[it] = downloadJob
                }
            }
            loadingJobs[imageView] = job
        }
    }

    fun cancelLoading(imageView: ImageView) {
        loadingJobs[imageView]?.cancel()
    }

    fun cancelDownloading(url: String) {
        downloadingJobs[url]?.cancel()
    }
}