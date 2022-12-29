package com.appe.db_room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Buku(
    @PrimaryKey
    val id: String,
    val judul_buku: String,
    val kategori: String,
    val kelas: String,
    val `file`: String,
    val time: String
)