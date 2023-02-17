package com.otraupe.imagesearch.ui.view.search

import com.otraupe.imagesearch.data.db.model.imageItem.ImageItem
import com.otraupe.imagesearch.data.db.model.search.Search

data class SearchUiState(
    val searchTerm: String? = null,
    var state: State = State.INIT,
    val images: List<ImageItem> = listOf(),
    var userMessageSent: Boolean = false
) {
    enum class State {
        INIT,
        IMAGES_FOUND,
        MORE_IMAGES_FOUND,
        NONE_FOUND,
        NO_MORE_FOUND,
        ERROR_API,
        ERROR_UNKNOWN,
        USER_MESSAGE_SENT
    }
    constructor(search: Search, state: State): this(
        searchTerm = search.term,
        state = state,
        images = search.imageItems.toList()
    )
}