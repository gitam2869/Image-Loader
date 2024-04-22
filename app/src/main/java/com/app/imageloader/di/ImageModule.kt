package com.app.imageloader.di

import android.content.Context
import com.app.imageloader.imageloading.ImageCache
import com.app.imageloader.imageloading.ImageDownloader
import com.app.imageloader.imageloading.ImageLoader
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ImageModule {

    @Provides
    @Singleton
    fun provideImageDownloader(): ImageDownloader {
        return ImageDownloader()
    }

    @Provides
    @Singleton
    fun provideImageCache(@ApplicationContext context: Context): ImageCache {
        return ImageCache(context)
    }

    @Provides
    fun provideImageLoader(
        imageDownloader: ImageDownloader,
        imageCache: ImageCache
    ): ImageLoader {
        return ImageLoader(imageDownloader, imageCache)
    }
}