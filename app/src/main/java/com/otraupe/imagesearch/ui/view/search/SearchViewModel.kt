package com.otraupe.imagesearch.ui.view.search

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import com.otraupe.imagesearch.data.network.API_HOME_URL
import com.otraupe.imagesearch.data.network.DEFAULT_PAGE_SIZE
import com.otraupe.imagesearch.data.network.DEFAULT_SEARCH_TERM
import com.otraupe.imagesearch.data.repository.ImageRepository
import com.otraupe.imagesearch.ext.spaceToPlus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.*
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val imageRepository: ImageRepository
    ) : ViewModel() {

    val providerHomeUrl: String = API_HOME_URL
    var currentSearchTerm: String = DEFAULT_SEARCH_TERM

    var currentLocale: Locale? = null
    var resetScrollPosition = false

    val scrollThreshold: Int
        get() = ( (imageListLiveData.value?.images?.size ?: 0) - (DEFAULT_PAGE_SIZE.toDouble() / 2).toInt() )
            .coerceAtLeast(10)

    val imageListLiveData : MutableLiveData<SearchUiState> = MutableLiveData(SearchUiState())

    init {
        viewModelScope.launch {
            imageListLiveData.asFlow().collect {
                Timber.Forest.d(it.images.let { images -> "Number of hits: ${images.size}" })
            }
        }
        imageRepository.liveData = imageListLiveData
        findImages(searchTerm = currentSearchTerm, override = true)
    }

    fun findImages(searchTerm: String, currentHitCount: Int = 0, override: Boolean = false) {
        if (searchTerm != currentSearchTerm || override ||
            imageListLiveData.value?.state == SearchUiState.State.ERROR_UNKNOWN) {
            currentSearchTerm = searchTerm
            viewModelScope.launch {
                resetScrollPosition = currentHitCount == 0
                imageRepository.fetchImages(
                    text = currentSearchTerm.spaceToPlus(),
                    locale = currentLocale ?: Locale.GERMAN,
                    currentHitCount = currentHitCount
                )
            }
        }
    }

    fun findMoreImages() {
        Timber.d("Loading more images for search \"$currentSearchTerm\"")
        imageListLiveData.value?.let {
            findImages(currentSearchTerm, it.images.size, true)
        }
    }
}