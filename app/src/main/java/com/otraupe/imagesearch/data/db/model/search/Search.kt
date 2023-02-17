package com.otraupe.imagesearch.data.db.model.search

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.otraupe.imagesearch.data.db.Converters
import com.otraupe.imagesearch.data.db.model.imageItem.ImageItem
import java.util.*

@Entity(tableName = "search_table")
@TypeConverters(Converters::class)
data class Search(

    @PrimaryKey(autoGenerate = false)
    val term: String,
    val totalHits: Long,
    val totalAvailable: Int,
    val searchDate: Date = Date()
    ) {
    @Ignore
    var imageItems: MutableList<ImageItem> = mutableListOf()
}