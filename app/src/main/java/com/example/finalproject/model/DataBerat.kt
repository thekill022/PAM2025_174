package com.example.finalproject.model

import kotlinx.serialization.Serializable

@Serializable
data class DataBerat(
    val id: Int,
    val user_id: Int,
    val berat: Float,
    val tanggal: String
)

@Serializable
data class BeratResponse (
    val success : Boolean,
    val data : List<DataBerat>
)