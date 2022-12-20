package com.appe.db_room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface SurahDao {

  @Insert
  suspend fun insertSurah(surah: Surah)

  @Update
  suspend fun updateSurah(surah: Surah)

  @Delete
  suspend fun deleteSurah(surah: Surah)

  @Query("DELETE FROM surah")
  suspend fun deleteAllSurah()

  @Query("SELECT * FROM surah")
  suspend fun getSurah(): List<Surah>
}