package com.app.imageloader.data.repository

import com.app.imageloader.data.api.APIServices
import com.app.imageloader.utils.NetworkResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ContentRepository @Inject constructor(private val apiServices: APIServices) {

    suspend fun getContent(limit: Int): NetworkResult<*> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiServices.getContent(limit)
                NetworkResult.Success(response)
            } catch (e: Exception) {
                NetworkResult.Error(e.message)
            }
        }
    }
}