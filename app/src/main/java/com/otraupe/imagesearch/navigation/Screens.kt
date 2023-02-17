package com.otraupe.imagesearch.navigation

sealed class Screens(val route: String) {
    object Search: Screens("search_view")
    object Detail: Screens("detail_view/{imageId}")
}