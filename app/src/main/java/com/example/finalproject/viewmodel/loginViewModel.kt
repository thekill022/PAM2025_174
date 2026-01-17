package com.example.finalproject.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.finalproject.model.LoginRequest
import com.example.finalproject.model.UIStateLogin
import com.example.finalproject.repository.RepositoryDataBerat
import com.example.finalproject.repository.RepositoryDataUser
import kotlinx.coroutines.launch

class LoginViewModel(
    private val repositoryDataUser: RepositoryDataUser,
    private val repositoryDataBerat: RepositoryDataBerat
) : ViewModel() {

    var uiState by mutableStateOf(UIStateLogin())
        private set

    fun updateUiState(email: String, password: String) {
        uiState = uiState.copy(
            email = email,
            password = password,
            isEntryValid = email.isNotEmpty() && password.isNotEmpty()
        )
    }

    fun login() {
        if (!uiState.isEntryValid) return

        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, errorMEssage = null)

            try {
                val request = LoginRequest(uiState.email, uiState.password)
                val resultLogin = repositoryDataUser.login(request)

                if (resultLogin.isSuccessful) {
                    val responseBody = resultLogin.body()

                    if (responseBody != null && responseBody.success) {

                        val loginData = responseBody.data
                        val token = loginData.token
                        val userAwal = loginData.user

                        var beratTerbaru = 0f

                        try {
                            val resultBerat = repositoryDataBerat.getBeratUser(userAwal.id)

                            if (resultBerat.isSuccessful) {
                                val listBerat = resultBerat.body()?.data ?: emptyList()

                                if (listBerat.isNotEmpty()) {
                                    val dataBeratTerbaru = listBerat.maxByOrNull { it.tanggal }
                                    beratTerbaru = dataBeratTerbaru?.berat ?: 0f
                                }
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }

                        val finalUser = userAwal.copy(berat = beratTerbaru)

                        repositoryDataUser.saveSession(token, finalUser)

                        uiState = uiState.copy(
                            isLoading = false,
                            isSuccess = true
                        )

                    } else {
                        uiState = uiState.copy(
                            isLoading = false,
                            errorMEssage = "Login gagal: Format respon tidak sesuai"
                        )
                    }
                } else {
                    uiState = uiState.copy(
                        isLoading = false,
                        errorMEssage = "Email atau Password salah"
                    )
                }
            } catch (e: Exception) {
                uiState = uiState.copy(
                    isLoading = false,
                    errorMEssage = "Gagal terhubung: ${e.message}"
                )
            }
        }
    }
}