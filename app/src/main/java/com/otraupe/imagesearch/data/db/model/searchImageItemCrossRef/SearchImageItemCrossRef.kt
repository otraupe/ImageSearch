package com.otraupe.imagesearch.data.db.model.searchImageItemCrossRef

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(primaryKeys = ["term", "imageId"])
data class SearchImageItemCrossRef(
    val term: String,

    @ColumnInfo(name = "imageId", index = true)
    val id: Long
    )