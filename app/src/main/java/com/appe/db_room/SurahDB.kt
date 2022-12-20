package com.appe.db_room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [Surah::class],
    version = 1
)

abstract class SurahDB: RoomDatabase() {

    abstract fun surahDao(): SurahDao

    companion object {
        @Volatile private var instance: SurahDB? = null
        private val LOCK = Any()

        operator fun invoke(context: Context) = instance?: synchronized(LOCK){
            instance?: buildDatabase(context).also {
                instance = it
            }
        }

        private fun buildDatabase(context: Context)= Room.databaseBuilder(
            context.applicationContext,
            SurahDB::class.java,
            "appe" // Nama Database
        ).build()
    }
}