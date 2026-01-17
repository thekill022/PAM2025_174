package com.example.finalproject.apiservice

import com.example.finalproject.model.FoodResponse
import com.example.finalproject.model.MealPlanReques
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface ServiceApiMeal {
    @POST("meal/planner")
    suspend fun getMealPlan(
        @Header("Authorization") token : String,
        @Body foodName : MealPlanReques
    ) : Response<FoodResponse>

}