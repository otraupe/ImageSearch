package com.otraupe.imagesearch.data.db.model.imageItem

import androidx.room.*

@Dao
interface ImageItemDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(image: ImageItem)

    @Query("DELETE FROM image_item_table WHERE id LIKE :id")
    fun deleteImage(id: Long)

    @Query("SELECT * FROM image_item_table WHERE id LIKE :id")
    fun getImage(id: Long): ImageItem?

    @RewriteQueriesToDropUnusedColumns
    @Query("SELECT * FROM " +
            "SearchImageItemCrossRef as _junction " +
            "INNER JOIN image_item_table ON (_junction.imageId = image_item_table.id) " +
            "WHERE _junction.term LIKE :term " +
            "LIMIT :size OFFSET :offset")
    fun getImagesForSearchPaged(term: String, size: Int, offset: Int): List<ImageItem>
}