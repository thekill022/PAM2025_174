package com.example.finalproject.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.finalproject.model.DataBerat
import com.example.finalproject.model.DataUser
import com.example.finalproject.repository.RepositoryDataBerat
import com.example.finalproject.repository.UserPreferenceRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

data class ProfileUiState(
    val user: DataUser = DataUser(
        id = 0,
        nama = "",
        email = "",
        password = "",
        usia = 0,
        tinggi_badan = 0,
        aktivitas_harian = "",
        jenis_kelamin = "",
        created_at = "",
        berat = 0f
    ),
    val beratHistory: List<DataBerat> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val showLogoutDialog: Boolean = false
)

class ProfileViewModel(
    private val userRepo: UserPreferenceRepository,
    private val beratRepo: RepositoryDataBerat
) : ViewModel() {

    var uiState by mutableStateOf(ProfileUiState())
        private set

    init {
        loadProfileData()
    }

    private fun loadProfileData() {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true)
            try {
                val user = userRepo.userData.first()
                uiState = uiState.copy(user = user)

                if (user.id != 0) {
                    loadBeratHistory(user.id)
                }
            } catch (e: Exception) {
                uiState = uiState.copy(
                    errorMessage = "Error: ${e.message}",
                    isLoading = false
                )
            }
        }
    }

    private suspend fun loadBeratHistory(userId: Int) {
        try {
            val response = beratRepo.getBeratUser(userId)
            if (response.isSuccessful) {
                val beratList = response.body()?.data ?: emptyList()
                // Sort by date descending and take last 12 months
                val sortedList = beratList.sortedBy { it.tanggal }.takeLast(12)
                uiState = uiState.copy(
                    beratHistory = sortedList,
                    isLoading = false
                )
            } else {
                uiState = uiState.copy(
                    beratHistory = emptyList(),
                    isLoading = false
                )
            }
        } catch (e: Exception) {
            uiState = uiState.copy(
                errorMessage = "Gagal memuat data berat: ${e.message}",
                isLoading = false
            )
        }
    }

    fun showLogoutDialog() {
        uiState = uiState.copy(showLogoutDialog = true)
    }

    fun hideLogoutDialog() {
        uiState = uiState.copy(showLogoutDialog = false)
    }

    fun logout(onLogoutSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                userRepo.logout()
                onLogoutSuccess()
            } catch (e: Exception) {
                uiState = uiState.copy(
                    errorMessage = "Gagal logout: ${e.message}"
                )
            }
        }
    }

    fun calculateBMI(): Float {
        val user = uiState.user
        if (user.berat == 0f || user.tinggi_badan == 0) return 0f
        val tinggiMeter = user.tinggi_badan / 100f
        return user.berat / (tinggiMeter * tinggiMeter)
    }

    fun getBMICategory(): String {
        val bmi = calculateBMI()
        return when {
            bmi == 0f -> "-"
            bmi < 18.5f -> "Kurus"
            bmi < 25f -> "Normal"
            bmi < 30f -> "Gemuk"
            else -> "Obesitas"
        }
    }

    fun getBeratProgress(): String {
        if (uiState.beratHistory.size < 2) return "Belum ada data"
        
        val latest = uiState.beratHistory.last().berat
        val previous = uiState.beratHistory[uiState.beratHistory.size - 2].berat
        val diff = latest - previous
        
        return when {
            diff > 0 -> "+${String.format("%.1f", diff)} kg"
            diff < 0 -> "${String.format("%.1f", diff)} kg"
            else -> "Stabil"
        }
    }
}
