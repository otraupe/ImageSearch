package com.otraupe.imagesearch.data.repository

import androidx.lifecycle.MutableLiveData
import com.otraupe.imagesearch.data.db.model.imageItem.ImageItem
import com.otraupe.imagesearch.data.db.model.imageItem.ImageItemDbRepository
import com.otraupe.imagesearch.data.model.ImageResponse
import com.otraupe.imagesearch.data.db.model.search.Search
import com.otraupe.imagesearch.data.db.model.search.SearchDbRepository
import com.otraupe.imagesearch.data.db.model.searchImageItemCrossRef.SearchImageItemCrossRef
import com.otraupe.imagesearch.data.db.model.searchImageItemCrossRef.SearchImageItemCrossRefRepository
import com.otraupe.imagesearch.data.network.*
import com.otraupe.imagesearch.ui.view.search.SearchUiState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber
import java.util.*
import javax.inject.Inject

class ImageRepository @Inject constructor(
    var searchDbRepository: SearchDbRepository,
    var imageItemDbRepository: ImageItemDbRepository,
    var searchImageItemCrossRefRepository: SearchImageItemCrossRefRepository
    ) {
    private val langMap = mapOf<Locale, String>(
        Pair(Locale.GERMAN, "de"),
        Pair(Locale.GERMANY, "de"),
    )
    private val apiInterface: ApiInterface = ApiClient.getApiClient().create(ApiInterface::class.java)
    private val scope = CoroutineScope(Dispatchers.Default)

    var liveData: MutableLiveData<SearchUiState>? = null

    init {
        // clear searches older than 24h
        scope.launch {
            val outdatedSearchTerms = searchDbRepository.findOutdatedSearches()
            Timber.d("Delete outdated searches: $outdatedSearchTerms")
            outdatedSearchTerms.forEach {
                searchDbRepository.deleteSearchByTerm(it)   //delete search first to prevent find ASAP
                val imageIds = searchImageItemCrossRefRepository.getImageIdsForTerm(it)
                searchImageItemCrossRefRepository.deleteCrossRefsByTerm(it)
                imageIds.forEach {it1 ->                    //delete images only if not associated with any search anymore
                    val terms = searchImageItemCrossRefRepository.getTermsForImageId(it1)
                    if (terms.isEmpty()) {
                        imageItemDbRepository.deleteImage(it1)
                    }
                }
                Timber.d("Outdated search \"$it\" deleted")
            }
        }
    }

    fun fetchImages(text: String, locale: Locale, currentHitCount: Int) {
        scope.launch {
            val search = searchDbRepository.getSearch(text)
            if (search == null) {                                   // search is not in db
                fetchRemoteImageHits(text, locale)
                return@launch
            }
            val images = imageItemDbRepository.getImagesForSearchPaged(text, DEFAULT_PAGE_SIZE, currentHitCount)
            if (images.isEmpty()) {
                if (currentHitCount == 0) {                         // search without any images in db
                    searchDbRepository.deleteSearch(search)
                    searchImageItemCrossRefRepository.deleteCrossRefsByTerm(search.term)
                    fetchRemoteImageHits(text, locale)
                    return@launch
                }
                if (currentHitCount % DEFAULT_PAGE_SIZE == 0) {     // try get another page from API
                    fetchRemoteImageHits(text, locale, (currentHitCount / DEFAULT_PAGE_SIZE) + 1)
                    return@launch
                }
                // 'odd' currentHitCount means end of API hits already reached
                liveData?.value?.let {
                    search.imageItems.addAll(it.images)             // keep already found images
                    liveData?.postValue(SearchUiState(search, SearchUiState.State.NO_MORE_FOUND))
                    return@launch
                }
            }
            liveData?.value?.let {
                val state: SearchUiState.State = if (currentHitCount > 0) {
                    search.imageItems.addAll(it.images)
                    SearchUiState.State.MORE_IMAGES_FOUND
                } else {
                    SearchUiState.State.IMAGES_FOUND
                }
                search.imageItems.addAll(images)
                liveData?.postValue(SearchUiState(search, state))
            }
        }
    }

    private fun fetchRemoteImageHits(text: String, locale: Locale, page: Int = 1) {

        val lang = langMap[locale] ?: DEFAULT_SEARCH_LANG

        apiInterface.fetchImageHits(apiKey = API_KEY, query = text, lang = lang, page = page)
            .enqueue(object : Callback<ImageResponse> {

                override fun onFailure(call: Call<ImageResponse>, t: Throwable) {
                    val images: MutableList<ImageItem> = mutableListOf()
                    liveData?.value?.let { images.addAll(it.images) }           // keep current image list state
                    liveData?.postValue(SearchUiState(searchTerm = text, state = SearchUiState.State.ERROR_UNKNOWN, images = images))
                }

                override fun onResponse(
                    call: Call<ImageResponse>,
                    response: Response<ImageResponse>
                ) {
                    val res = response.body()
                    Timber.d("Queried page $page of ${res?.total} total images matching \"$text\"")
                    val images: MutableList<ImageItem> = mutableListOf()
                    if (response.code() != 200 || res == null) {
                        liveData?.value?.let { images.addAll(it.images) }       // keep current image list state
                        liveData?.postValue(SearchUiState(searchTerm = text,
                            state = SearchUiState.State.ERROR_API, images = images))   // TODO: present user message (when can it happen?)
                        return
                    }
                    if (res.hits.isEmpty()) {
                        val state: SearchUiState.State = if (page > 1) {
                            liveData?.value?.let { images.addAll(it.images) }
                            SearchUiState.State.NO_MORE_FOUND
                        } else {
                            SearchUiState.State.NONE_FOUND
                        }
                        liveData?.postValue(SearchUiState(searchTerm = text, state = state, images = images))
                        return
                    }
                    val state: SearchUiState.State = if (page > 1) {
                        liveData?.value?.let { images.addAll(it.images) }
                        SearchUiState.State.MORE_IMAGES_FOUND
                    } else {
                        SearchUiState.State.IMAGES_FOUND
                    }
                    images.addAll(res.hits.map { ImageItem(it) })
                    liveData?.postValue(SearchUiState(searchTerm = text, state = state, images = images))

                    // store in db
                    scope.launch {
                        res.hits.forEach {
                            imageItemDbRepository.insertImage(ImageItem(it))
                            searchImageItemCrossRefRepository.insertCrossRef(
                                SearchImageItemCrossRef(term = text, id = it.id)
                            )
                        }
                        searchDbRepository.insertSearch(Search(term = text,     // insert search when sub-data is complete
                            totalHits = res.total, totalAvailable = res.totalHits))
                    }
                }
            })
    }
}