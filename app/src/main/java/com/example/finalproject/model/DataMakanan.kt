package com.example.finalproject.model

import kotlinx.serialization.Serializable

@Serializable
data class DataMakanan(
    val id: Int = 0,
    val user_id: Int,
    val nama: String,
    val kategori: String,
    val kalori: Int,
    val protein: Int,
    val karbo: Int,
    val lemak: Int,
    val quantity: Int = 1,
    val hari_tanggal: String
)

@Serializable
data class MakananResponse(
    val success: Boolean,
    val message: String,
    val data: List<DataMakanan>
)

@Serializable
data class GeneralResponse(
    val success: Boolean,
    val message: String
)