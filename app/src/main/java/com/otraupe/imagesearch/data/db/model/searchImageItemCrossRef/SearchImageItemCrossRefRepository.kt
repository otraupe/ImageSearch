package com.otraupe.imagesearch.data.db.model.searchImageItemCrossRef

import javax.inject.Inject

class SearchImageItemCrossRefRepository @Inject constructor(private val crossRefDao: SearchImageItemCrossRefDao){

    suspend fun insertCrossRef(crossRef: SearchImageItemCrossRef) = crossRefDao.insert(crossRef)

    suspend fun deleteCrossRefsByTerm(term: String) = crossRefDao.deleteCrossRefs(term)

    suspend fun getImageIdsForTerm(term: String) = crossRefDao.getImageIdsForTerm(term)

    suspend fun getTermsForImageId(id: Long) = crossRefDao.getTermsForImageId(id)
}