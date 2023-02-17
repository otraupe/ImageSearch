package com.otraupe.imagesearch.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.otraupe.imagesearch.data.db.model.imageItem.ImageItem
import com.otraupe.imagesearch.data.db.model.imageItem.ImageItemDao
import com.otraupe.imagesearch.data.db.model.search.Search
import com.otraupe.imagesearch.data.db.model.search.SearchDao
import com.otraupe.imagesearch.data.db.model.searchImageItemCrossRef.SearchImageItemCrossRef
import com.otraupe.imagesearch.data.db.model.searchImageItemCrossRef.SearchImageItemCrossRefDao
import timber.log.Timber

@Database(entities = [Search::class, ImageItem::class, SearchImageItemCrossRef::class], version = 1)
abstract class RoomDb : RoomDatabase() {

    abstract fun searchDao(): SearchDao
    abstract fun imageItemDao(): ImageItemDao
    abstract fun searchImageItemCrossRefDao(): SearchImageItemCrossRefDao

    companion object {
        private var instance: RoomDb? = null

        @Synchronized
        fun getInstance(ctx: Context): RoomDb {
            if(instance == null)
                instance = Room.databaseBuilder(ctx.applicationContext, RoomDb::class.java,
                    "image_search_database")
                    .fallbackToDestructiveMigration()
                    .addCallback(roomCallback)
                    .build()

            return instance!!
        }

        private val roomCallback = object : Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                Timber.d("Room DB created, version is :" + db.version.toString())
                super.onCreate(db)
            }
        }
    }
}