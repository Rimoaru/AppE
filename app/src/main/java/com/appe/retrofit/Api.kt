package com.appe.retrofit

import com.appe.db_room.Buku
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface Api {
    @GET("buku")
    suspend fun getBuku(): Response<ArrayList<Buku>>

}