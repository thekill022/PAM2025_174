package com.example.finalproject.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.finalproject.model.DataUser
import com.example.finalproject.model.LoginData
import com.example.finalproject.model.LoginRequest
import com.example.finalproject.model.RegisterUiState
import com.example.finalproject.repository.RepositoryDataUser
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class RegisterViewModel(private val repositoryDataUser: RepositoryDataUser) : ViewModel() {

    var uiState by mutableStateOf(RegisterUiState())
        private set

    fun updateUiState(
        nama: String? = null,
        usia: String? = null,
        email: String? = null,
        password: String? = null,
        tinggi: String? = null,
        berat: String? = null,
        aktivitas: String? = null,
        gender: String? = null
    ) {
        uiState = uiState.copy(
            nama = nama ?: uiState.nama,
            usia = usia ?: uiState.usia,
            email = email ?: uiState.email,
            password = password ?: uiState.password,
            tinggiBadan = tinggi ?: uiState.tinggiBadan,
            beratBadan = berat ?: uiState.beratBadan,
            aktivitasHarian = aktivitas ?: uiState.aktivitasHarian,
            jenis_kelamin = gender ?: uiState.jenis_kelamin
        )
    }

    fun register() {
        if (!validateInput()) return

        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, errorMessage = null)

            try {
                val usiaInt = uiState.usia.toIntOrNull() ?: 0
                val tinggiInt = uiState.tinggiBadan.toIntOrNull() ?: 0
                val beratFloat = uiState.beratBadan.toFloatOrNull() ?: 0.0f

                val dataUserBaru = DataUser(
                    id = 0,
                    nama = uiState.nama,
                    usia = usiaInt,
                    email = uiState.email,
                    password = uiState.password,
                    tinggi_badan = tinggiInt,
                    aktivitas_harian = uiState.aktivitasHarian,
                    created_at = "",
                    jenis_kelamin = uiState.jenis_kelamin,
                    berat = beratFloat
                )

                val registerResponse = repositoryDataUser.createUser(dataUserBaru)

                if (registerResponse.isSuccessful) {
                    performAutoLogin(uiState.email, uiState.password)
                } else {
                    val errorBody = registerResponse.errorBody()?.string() ?: "Gagal Register"
                    uiState = uiState.copy(isLoading = false, errorMessage = errorBody)
                }

            } catch (e: IOException) {
                uiState = uiState.copy(isLoading = false, errorMessage = "Tidak ada koneksi internet")
            } catch (e: HttpException) {
                uiState = uiState.copy(isLoading = false, errorMessage = "Gagal memuat data server")
            } catch (e: Exception) {
                uiState = uiState.copy(isLoading = false, errorMessage = "Error: ${e.message}")
            }
        }
    }

    private suspend fun performAutoLogin(email: String, pass: String) {
        try {
            val loginData = LoginRequest(email, pass)

            val loginResponse = repositoryDataUser.login(loginData)

            if (loginResponse.isSuccessful && loginResponse.body() != null) {
                uiState = uiState.copy(
                    isLoading = false,
                    isSuccess = true
                )
            } else {
                uiState = uiState.copy(
                    isLoading = false,
                    errorMessage = "Register berhasil, silakan Login manual."
                )
            }
        } catch (e: Exception) {
            uiState = uiState.copy(
                isLoading = false,
                errorMessage = "Gagal login otomatis: ${e.message}"
            )
        }
    }

    private fun validateInput(): Boolean {
        if (uiState.nama.isBlank() || uiState.email.isBlank() || uiState.password.isBlank()) {
            uiState = uiState.copy(errorMessage = "Semua data wajib diisi!")
            return false
        }
        if (uiState.usia.toIntOrNull() == null || uiState.tinggiBadan.toIntOrNull() == null) {
            uiState = uiState.copy(errorMessage = "Usia dan Tinggi Badan harus angka!")
            return false
        }
        return true
    }

}