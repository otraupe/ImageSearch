package com.otraupe.imagesearch.data.db.model.search

import androidx.room.*

@Dao
interface SearchDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(search: Search)

    @Delete
    fun delete(search: Search)

    @Query("DELETE FROM search_table WHERE term LIKE :term")
    fun deleteSearch(term: String)

    @Query("SELECT term FROM search_table WHERE searchDate < :dateTime")
    fun findOutdatedSearches(dateTime: Long): List<String>

    @Query("SELECT * FROM search_table WHERE term LIKE :term")
    fun getSearch(term: String): Search?
}