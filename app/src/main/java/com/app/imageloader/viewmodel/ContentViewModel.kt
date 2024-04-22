package com.app.imageloader.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.imageloader.data.dataclasses.Content
import com.app.imageloader.data.repository.ContentRepository
import com.app.imageloader.utils.ApiResult
import com.app.imageloader.utils.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ContentViewModel @Inject constructor(private val contentRepository: ContentRepository) :
    ViewModel() {
    private val _contentResponse: MutableLiveData<ApiResult<Content>> = MutableLiveData()
    val contentResponse: LiveData<ApiResult<Content>>
        get() = _contentResponse

    fun getContent(limit: Int) {
        _contentResponse.value = ApiResult.Loading(true)
        viewModelScope.launch {
            when (val result = contentRepository.getContent(limit)) {
                is NetworkResult.Success -> {
                    _contentResponse.value = ApiResult.Success(result.data as Content)
                    _contentResponse.value = ApiResult.Loading(false)
                }

                is NetworkResult.Error -> {
                    _contentResponse.value = ApiResult.Error(result.message)
                    _contentResponse.value = ApiResult.Loading(false)
                }
            }
        }
    }
}