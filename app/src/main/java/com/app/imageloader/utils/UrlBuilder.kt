package com.app.imageloader.utils

object UrlBuilder {

    fun prepareImageUrl(domain: String?, basePath: String?, mediaType: String?, key: String?): String {
        return "$domain/$basePath/$mediaType/$key"
    }
}