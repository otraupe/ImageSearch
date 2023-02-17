package com.otraupe.imagesearch.di

import android.content.Context
import com.otraupe.imagesearch.data.db.RoomDb
import com.otraupe.imagesearch.data.db.model.imageItem.ImageItemDao
import com.otraupe.imagesearch.data.db.model.imageItem.ImageItemDbRepository
import com.otraupe.imagesearch.data.db.model.search.SearchDbRepository
import com.otraupe.imagesearch.data.db.model.search.SearchDao
import com.otraupe.imagesearch.data.db.model.searchImageItemCrossRef.SearchImageItemCrossRefDao
import com.otraupe.imagesearch.data.db.model.searchImageItemCrossRef.SearchImageItemCrossRefRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@Module
object DBModule {

    @Provides
    fun provideSearchDao(@ApplicationContext appContext: Context) : SearchDao {
        return RoomDb.getInstance(appContext).searchDao()
    }
    @Provides
    fun provideSearchDBRepository(searchDao: SearchDao) = SearchDbRepository(searchDao)

    @Provides
    fun provideImageItemDao(@ApplicationContext appContext: Context) : ImageItemDao {
        return RoomDb.getInstance(appContext).imageItemDao()
    }
    @Provides
    fun provideImageItemDBRepository(imageItemDao: ImageItemDao) = ImageItemDbRepository(imageItemDao)

    @Provides
    fun provideSearchImageItemCrossRefDao(@ApplicationContext appContext: Context) : SearchImageItemCrossRefDao {
        return RoomDb.getInstance(appContext).searchImageItemCrossRefDao()
    }
    @Provides
    fun provideSearchImageItemCrossRefRepository(searchImageItemCrossRefDao: SearchImageItemCrossRefDao) = SearchImageItemCrossRefRepository(searchImageItemCrossRefDao)
}