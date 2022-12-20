package com.appe.retrofit

import com.appe.db_room.Surah
import retrofit2.Response
import retrofit2.http.GET

interface Api {
    @GET("surah")
    suspend fun getSurah(): Response<ArrayList<Surah>>
}