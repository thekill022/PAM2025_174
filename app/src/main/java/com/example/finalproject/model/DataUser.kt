package com.example.finalproject.model
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DataUser(
    val id: Int = 0,
    val nama: String,
    val usia: Int,
    val email: String,
    val password: String = "",
    val tinggi_badan: Int,
    val aktivitas_harian: String,
    val jenis_kelamin : String,
    val created_at: String,
    val berat : Float = 0.0f
)

@Serializable
data class LoginRequest(
    val email: String,
    val password: String
)

@Serializable
data class LoginData(
    val token: String,
    val user : DataUser
)

@Serializable
data class LoginResponse(
    val success : Boolean,
    val data : LoginData
)

data class DetailUser(
    val id: Int = 0,
    val nama: String = "",
    val usia: Int = 0,
    val email: String = "",
    val password: String = "",
    val tinggi_badan: Int = 0,
    val aktivitas_harian: String = "",
    val jenis_kelamin : String = "",
    val created_at: String = "",
    val berat : Float = 0.0F
)

data class UIStateUser(
    val detailUser : DetailUser = DetailUser(),
    val isEntryValid : Boolean = false
)

data class  UIStateLogin(
    val email : String = "",
    val password : String = "",
    val isLoading : Boolean = false,
    val isSuccess : Boolean = false,
    val errorMEssage : String? = null,
    val isEntryValid : Boolean = false
)

data class RegisterUiState(
    val nama: String = "",
    val usia: String = "",
    val email: String = "",
    val password: String = "",
    val tinggiBadan: String = "",
    val beratBadan: String = "",
    val aktivitasHarian: String = "",
    val jenis_kelamin : String = "",
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null
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
    jenis_kelamin = jenis_kelamin,
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
    jenis_kelamin = jenis_kelamin,
    created_at = created_at,
    berat = berat
)

fun DataUser.toUiStateUser(isEntryValid: Boolean) :UIStateUser = UIStateUser(
    detailUser = this.toDetailUser(),
    isEntryValid = isEntryValid
)