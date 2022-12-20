package com.appe.db_room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Surah(
    @PrimaryKey
    val id: String,
    val nama_surah: String,
    val jml_ayat: Int,
    val `file`: String,
    val time: String
)