package com.example.finalproject.repository

import com.example.finalproject.apiservice.ServiceApiUsers
import com.example.finalproject.model.DataUser
import com.example.finalproject.model.LoginData
import com.example.finalproject.model.LoginResponse
import retrofit2.Response

interface RepositoryDataUser {
suspend fun createUser(dataUser: DataUser) : retrofit2.Response<Void>
suspend fun login(loginData: LoginData) : retrofit2.Response<LoginResponse>
suspend fun getSatuUser(id : Int) : DataUser
}

class JaringanRepositoryDataUser(
    private val serviceApiUsers : ServiceApiUsers
) :RepositoryDataUser {
    override suspend fun createUser(dataUser: DataUser): Response<Void> = serviceApiUsers.createUser(dataUser)
    override suspend fun login(loginData: LoginData): Response<LoginResponse>  = serviceApiUsers.login(loginData)
    override suspend fun getSatuUser(id: Int): DataUser = serviceApiUsers.getSatuUser(id)
}