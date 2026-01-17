package com.example.finalproject.apiservice

import com.example.finalproject.model.DataMakanan
import com.example.finalproject.model.GeneralResponse
import com.example.finalproject.model.MakananResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ServiceApiMakanan {
    @POST("food")
    suspend fun addMakanan(
        @Header("Authorization") token: String,
        @Body makanan: DataMakanan
    ): Response<GeneralResponse>

    @GET("food/{id}")
    suspend fun getMakanan(
        @Header("Authorization") token: String,
        @Path("id") userId: Int,
        @Query("tanggal") tanggal: String
    ): Response<MakananResponse>

    @DELETE("food/{id}")
    suspend fun deleteMakanan(
        @Header("Authorization") token: String,
        @Path("id") foodId: Int
    ): Response<GeneralResponse>
}