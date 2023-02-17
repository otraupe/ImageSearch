package com.otraupe.imagesearch.data.db.model.imageItem

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.otraupe.imagesearch.data.model.ImageHit

@Entity(tableName = "image_item_table")
data class ImageItem(

    @PrimaryKey(autoGenerate = false)
    val id: Long,
    val pageURL: String,
    val type: String,
    val tags: String,
    val previewURL: String,
    val largeImageURL: String,   // TODO: determine image size for app's full view dynamically (depending on screen size)
    val views: Long,
    val downloads: Long,
    val likes: Long,
    val comments: Long,
    val userId: Long,
    val user: String,
    val userImageURL: String
    ) {
    constructor(hit: ImageHit): this(
        id = hit.id,
        pageURL = hit.pageURL,
        type = hit.type,
        tags = hit.tags,
        previewURL = hit.previewURL,
        largeImageURL = hit.largeImageURL,
        views = hit.views,
        downloads = hit.downloads,
        likes = hit.likes,
        comments = hit.comments,
        userId = hit.user_id,
        user = hit.user,
        userImageURL = hit.userImageURL
    )
}