package com.app.imageloader.data.api

import com.app.imageloader.data.dataclasses.Content
import retrofit2.http.GET
import retrofit2.http.Query

interface APIServices {

    @GET("api/v2/content/misc/media-coverages")
    suspend fun getContent(@Query("limit") limit: Int) : Content
}