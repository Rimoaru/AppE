package com.appe.retrofit

import com.appe.db_room.Buku
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Streaming

interface Api {
    @Streaming
    @GET("buku")
    suspend fun getBuku(): Response<ArrayList<Buku>>

}