package com.example.finalproject.model
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DataUser(
    val id: Int = 0,
    val nama: String,
    val usia: Int,
    val email: String,
    val password: String,
    val tinggi_badan: Int,
    val aktivitas_harian: String,
    val created_at: String,
    val berat : Float = 0.0f
)

@Serializable
data class LoginData(
    val email : String,
    val password : String
)

@Serializable
data class LoginResponse(
    val token : String,
    @SerialName("userWithoutPassword")
    val user : DataUser
)

data class DetailUser(
    val id: Int = 0,
    val nama: String = "",
    val usia: Int = 0,
    val email: String = "",
    val password: String = "",
    val tinggi_badan: Int = 0,
    val aktivitas_harian: String = "",
    val created_at: String = "",
    val berat : Float = 0.0F
)

data class UIStateUser(
    val detailUser : DetailUser = DetailUser(),
    val isEntryValid : Boolean = false
)

// method methodnya ni
fun DetailUser.toDataUser() : DataUser = DataUser(
    id = id,
    nama = nama,
    usia = usia,
    email = email,
    password = password,
    tinggi_badan = tinggi_badan,
    aktivitas_harian = aktivitas_harian,
    created_at = created_at,
    berat = berat
)

fun DataUser.toDetailUser() : DetailUser = DetailUser(
    id = id,
    nama = nama,
    usia = usia,
    email = email,
    password = password,
    tinggi_badan = tinggi_badan,
    aktivitas_harian = aktivitas_harian,
    created_at = created_at,
    berat = berat
)

fun DataUser.toUiStateUser(isEntryValid: Boolean) :UIStateUser = UIStateUser(
    detailUser = this.toDetailUser(),
    isEntryValid = isEntryValid
)