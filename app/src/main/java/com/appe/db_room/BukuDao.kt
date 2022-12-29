package com.appe.db_room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface BukuDao {

    @Insert
    suspend fun insertBuku(buku: Buku)

    @Update
    suspend fun updateSBuku(buku: Buku)

    @Delete
    suspend fun deleteBuku(buku: Buku)

    @Query("DELETE FROM buku")
    suspend fun deleteAllBuku()

    @Query("SELECT * FROM buku")
    suspend fun getBuku(): List<Buku>
}