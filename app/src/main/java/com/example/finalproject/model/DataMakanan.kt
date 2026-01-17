package com.example.finalproject.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class DataMakanan(
    @Transient val id: Int = 0,  // Transient = tidak dikirim ke backend
    val user_id: Int,
    val nama: String,
    val kategori: String,
    val kalori: Int,
    val protein: Int,
    val karbo: Int,
    val lemak: Int,
    val serat : Int,
    val quantity: String,
    val hari_tanggal: String
)

@Serializable
data class MakananResponse(
    val success: Boolean,
    val message: String,
    val data: List<DataMakanan>
)

@Serializable
data class AddMakananResponse(
    val success: Boolean,
    val data: AddMakananData
)

@Serializable
data class AddMakananData(
    val success: Boolean,
    val message: String,
    val insertId: Int
)

@Serializable
data class GeneralResponse(
    val success: Boolean,
    val message: String
)

//ini buat yang ai meal plan yak
@Serializable
data class DataMealPlan (
    val meals : List<meals>,
    @SerialName("total_nutrition")
    val totalNutrition : totalNutrition
)

@Serializable
data class meals (
    val nama:String,
    val kategori: String,
    val kalori:Int,
    val protein: Float,
    val karbo: Float,
    val lemak: Float,
    val serat: Float,
    val quantity:String
)

@Serializable
data class totalNutrition (
    val kalori:Int,
    val protein: Int,
    val karbo: Int,
    val lemak: Int,
    val serat: Int
)

@Serializable
data class FoodResponse (
    val success : Boolean,
    val data : DataMealPlan
)

@Serializable
data class MealPlanReques(
    val foodName : String
)

// Models untuk Food Calorie dari AI (manual input)
@Serializable
data class FoodCalorieRequest(
    val foodName: String
)

@Serializable
data class FoodCalorieResponse(
    val success: Boolean,
    val data: FoodCalorieData
)

@Serializable
data class FoodCalorieData(
    @SerialName("food_items")
    val foodItems: List<FoodItem>,
    val total: TotalNutritionCalorie
)

@Serializable
data class FoodItem(
    val name: String,
    val protein: Float,
    val carbohydrates: Float,
    val fat: Float,
    val calories: Int,
    val fiber: Float,
    val quantity: String
)

@Serializable
data class TotalNutritionCalorie(
    val protein: Float,
    val carbohydrates: Float,
    val fat: Float,
    val calories: Int,
    val fiber: Float,
    val quantity: String
)