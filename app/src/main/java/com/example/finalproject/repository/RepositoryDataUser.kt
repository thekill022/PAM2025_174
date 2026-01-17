package com.example.finalproject.repository

import com.example.finalproject.apiservice.ServiceApiUsers
import com.example.finalproject.model.DataUser
import com.example.finalproject.model.LoginData
import com.example.finalproject.model.LoginRequest
import com.example.finalproject.model.LoginResponse
import retrofit2.Response

interface RepositoryDataUser {
suspend fun createUser(dataUser: DataUser) : retrofit2.Response<Void>
suspend fun login(loginRequest: LoginRequest) : retrofit2.Response<LoginResponse>
suspend fun getSatuUser(id : Int) : DataUser
suspend fun saveSession(token: String, user: DataUser)
}

class JaringanRepositoryDataUser(
    private val serviceApiUsers : ServiceApiUsers,
    private val userPreferenceRepository: UserPreferenceRepository
) :RepositoryDataUser {
    override suspend fun createUser(dataUser: DataUser): Response<Void> = serviceApiUsers.createUser(dataUser)
    override suspend fun login(loginRequest: LoginRequest): Response<LoginResponse>  {
        val response = serviceApiUsers.login(loginRequest)

        if (response.isSuccessful) {
            val body = response.body()
            if (body != null) {
                userPreferenceRepository.saveSession(
                    token = body.data.token,
                    user = body.data.user
                )
            }
        }
        return response
    }
    override suspend fun getSatuUser(id: Int): DataUser = serviceApiUsers.getSatuUser(id)
    override suspend fun saveSession(token: String, user: DataUser) {
        userPreferenceRepository.saveSession(token, user)
    }
}