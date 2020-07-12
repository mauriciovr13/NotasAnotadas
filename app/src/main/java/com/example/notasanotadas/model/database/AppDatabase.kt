package com.example.notasanotadas.model.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [Note::class], version = 1)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun noteDAO() : NoteDAO

    companion object {
        private var INSTANCE : AppDatabase? = null

        @Synchronized
        fun getInstance(context : Context) : AppDatabase {
            if (INSTANCE === null) {
                INSTANCE = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "database.db"
                ).allowMainThreadQueries().build()
            }

            return INSTANCE as AppDatabase
        }
    }
}