package com.example.finalproject.repository

import com.example.finalproject.apiservice.ServiceApiBerat
import com.example.finalproject.model.BeratResponse
import com.example.finalproject.model.DataMakanan
import com.example.finalproject.model.GeneralResponse
import com.example.finalproject.model.MakananResponse
import kotlinx.coroutines.flow.first
import retrofit2.Response

interface RepositoryDataBerat {
    suspend fun getBeratUser(id : Int): Response<BeratResponse>
}

class JaringanRepositoryBerat(
    private val userPreferenceRepository: UserPreferenceRepository,
    private val serviceApiBerat: ServiceApiBerat
) : RepositoryDataBerat {
    private suspend fun getToken(): String {
        val token = userPreferenceRepository.userToken.first() ?: ""
        return "Bearer $token"
    }
    override suspend fun getBeratUser(id: Int): Response<BeratResponse> {
        val token = getToken()
        return serviceApiBerat.getBeratUser(token, id)
    }

}