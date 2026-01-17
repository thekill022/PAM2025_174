package com.example.finalproject.repository

import com.example.finalproject.apiservice.ServiceApiMakanan
import com.example.finalproject.model.DataMakanan
import com.example.finalproject.model.GeneralResponse
import com.example.finalproject.model.MakananResponse
import kotlinx.coroutines.flow.first
import retrofit2.Response

interface RepositoryDataMakanan {
    suspend fun addMakanan(dataMakanan: DataMakanan): Response<GeneralResponse>
    suspend fun getMakanan(userId: Int, tanggal: String): Response<MakananResponse>
    suspend fun deleteMakanan(id: Int): Response<GeneralResponse>
}

class JaringanRepositoryMakanan(
    private val serviceApiMakanan: ServiceApiMakanan,
    private val userPreferenceRepository: UserPreferenceRepository
) : RepositoryDataMakanan {

    private suspend fun getToken(): String {
        val token = userPreferenceRepository.userToken.first() ?: ""
        return "Bearer $token"
    }

    override suspend fun addMakanan(dataMakanan: DataMakanan): Response<GeneralResponse> {
        val token = getToken()
        return serviceApiMakanan.addMakanan(token, dataMakanan)
    }

    override suspend fun getMakanan(userId: Int, tanggal: String): Response<MakananResponse> {
        val token = getToken()
        return serviceApiMakanan.getMakanan(token, userId, tanggal)
    }

    override suspend fun deleteMakanan(id: Int): Response<GeneralResponse> {
        val token = getToken()
        return serviceApiMakanan.deleteMakanan(token, id)
    }
}