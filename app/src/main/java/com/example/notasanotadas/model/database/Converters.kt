package com.example.notasanotadas.model.database

import androidx.room.TypeConverter
import com.example.notasanotadas.model.DateTime
import java.util.*

class Converters {

    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time?.toLong()
    }

    @TypeConverter fun dateTimeToString(date: DateTime): String? {
        return date?.formatToSave()
    }

    @TypeConverter fun stringToDateTime(date: String): DateTime? {
        var dateSeparated = date.split("-")
        var day = dateSeparated[0].toInt()
        var month: Int = dateSeparated[1].toInt()
        var year: Int = dateSeparated[2].toInt()
        var hour: Int = dateSeparated[3].toInt()
        var minute: Int = dateSeparated[4].toInt()
        return DateTime(day, month, year, hour, minute)
    }
}