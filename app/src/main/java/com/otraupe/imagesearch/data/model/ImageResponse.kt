package com.otraupe.imagesearch.data.model

data class ImageResponse(
    val total: Long,
    val totalHits: Int,
    val hits: List<ImageHit>
    )