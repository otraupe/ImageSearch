package com.otraupe.imagesearch.data.db.model.imageItem

import javax.inject.Inject

class ImageItemDbRepository @Inject constructor(private val imageItemDao: ImageItemDao){

    suspend fun insertImage(image: ImageItem) = imageItemDao.insert(image)

    suspend fun deleteImage(id: Long) = imageItemDao.deleteImage(id)

    suspend fun getImage(imageId: Long) = imageItemDao.getImage(imageId)

    suspend fun getImagesForSearchPaged(term: String, size: Int, offset: Int) = imageItemDao.getImagesForSearchPaged(term, size, offset)
}