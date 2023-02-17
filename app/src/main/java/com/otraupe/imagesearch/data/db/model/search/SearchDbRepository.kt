package com.otraupe.imagesearch.data.db.model.search

import org.joda.time.DateTime
import javax.inject.Inject

class SearchDbRepository @Inject constructor(private val searchDao: SearchDao){

    suspend fun insertSearch(search: Search) = searchDao.insert(search)

    suspend fun deleteSearch(search: Search) = searchDao.delete(search)

    suspend fun deleteSearchByTerm(term: String) = searchDao.deleteSearch(term)

    suspend fun findOutdatedSearches(): List<String> {
        val dateTime = DateTime().minusDays(1).toDate()
        return searchDao.findOutdatedSearches(dateTime.time)
    }

    suspend fun getSearch(searchTerm: String) = searchDao.getSearch(searchTerm)
}