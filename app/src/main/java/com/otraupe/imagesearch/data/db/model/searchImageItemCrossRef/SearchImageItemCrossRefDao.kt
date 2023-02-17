package com.otraupe.imagesearch.data.db.model.searchImageItemCrossRef

import androidx.room.*

@Dao
interface SearchImageItemCrossRefDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(crossRef: SearchImageItemCrossRef)

    @Query("DELETE FROM SearchImageItemCrossRef WHERE term LIKE :term")
    fun deleteCrossRefs(term: String)

    @Query("SELECT imageId FROM SearchImageItemCrossRef WHERE term LIKE :term")
    fun getImageIdsForTerm(term: String): List<Long>

    @Query("SELECT term FROM SearchImageItemCrossRef WHERE imageId LIKE :id")
    fun getTermsForImageId(id: Long): List<String>
}