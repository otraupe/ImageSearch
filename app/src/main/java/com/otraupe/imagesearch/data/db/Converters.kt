package com.otraupe.imagesearch.data.db

import androidx.room.TypeConverter
import java.util.*

class Converters {

    @TypeConverter
    fun toDate(dateLong: Long?): Date? {
        return if (dateLong == null) null else Date(dateLong)
    }

    @TypeConverter
    fun fromDate(date: Date?): Long? {
        return date?.time
    }
}