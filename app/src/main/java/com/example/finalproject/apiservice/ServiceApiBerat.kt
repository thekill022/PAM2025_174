package com.example.finalproject.apiservice

import com.example.finalproject.model.BeratResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

interface ServiceApiBerat {
    @GET("berat/{userId}")
    suspend fun getBeratUser(
        @Header("Authorization") token: String,
        @Path("userId") userId: Int
    ): Response<BeratResponse>
}