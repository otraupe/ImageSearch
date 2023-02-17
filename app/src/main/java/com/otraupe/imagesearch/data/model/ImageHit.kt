package com.otraupe.imagesearch.data.model

data class ImageHit(
    val id: Long,
    val pageURL: String,
    val type: String,
    val tags: String,
    val previewURL: String,
    val previewWidth: Int,
    val previewHeight: Int,
    val webformatURL: String,
    val webformatWidth: Int,
    val webformatHeight: Int,
    val largeImageURL: String,
    val fullHDURL: String,
    val imageURL: String,
    val imageWidth: Int,
    val imageHeight: Int,
    val imageSize: Long,
    val views: Long,
    val downloads: Long,
    val likes: Long,
    val comments: Long,
    val user_id: Long,
    val user: String,
    val userImageURL: String
    )