package com.example.finalproject.apiservice

import com.example.finalproject.model.DataUser
import com.example.finalproject.model.LoginData
import com.example.finalproject.model.LoginResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ServiceApiUsers {

    @POST("register")
    suspend fun createUser(@Body dataUser: DataUser):retrofit2.Response<Void>

    @POST("login")
    suspend fun login(@Body loginData : LoginData): retrofit2.Response<LoginResponse>

    @GET("user/{id}")
    suspend fun getSatuUser(@Path("id") id : Int) : DataUser

}