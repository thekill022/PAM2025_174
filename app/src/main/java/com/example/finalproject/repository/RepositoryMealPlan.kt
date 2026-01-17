package com.example.finalproject.repository

import com.example.finalproject.apiservice.ServiceApiMeal
import com.example.finalproject.model.DataMealPlan
import com.example.finalproject.model.MealPlanReques
import kotlinx.coroutines.flow.first

interface RepositoryMealPlan {
    suspend fun generateMealPlan(prompt: String): DataMealPlan?
}

class JaringanRepositoryMealPlan(
    private val serviceApiMeal: ServiceApiMeal,
    private val userPreferenceRepository: UserPreferenceRepository
) : RepositoryMealPlan {

    override suspend fun generateMealPlan(prompt: String): DataMealPlan? {
        val token = userPreferenceRepository.userToken.first() ?: ""

        try {
            val request = MealPlanReques(foodName = prompt)
            val response = serviceApiMeal.getMealPlan("Bearer $token", request)

            if (response.isSuccessful) {
                return response.body()?.data
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }
}