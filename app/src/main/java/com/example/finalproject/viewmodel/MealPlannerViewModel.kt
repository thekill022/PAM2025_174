package com.example.finalproject.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.finalproject.model.DataMakanan
import com.example.finalproject.model.DataMealPlan
import com.example.finalproject.model.DataUser
import com.example.finalproject.repository.RepositoryDataMakanan
import com.example.finalproject.repository.RepositoryMealPlan
import com.example.finalproject.repository.UserPreferenceRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

sealed interface MealUiState {
    object Idle : MealUiState
    object Loading : MealUiState
    data class Success(val data: DataMealPlan) : MealUiState
    data class Error(val message: String) : MealUiState
}

class MealPlannerViewModel(
    private val repository: RepositoryMealPlan,
    private val userRepo: UserPreferenceRepository,
    private val makananRepo: RepositoryDataMakanan
) : ViewModel() {

    var promptText by mutableStateOf("")
        private set

    var uiState: MealUiState by mutableStateOf(MealUiState.Idle)
        private set
    var saveMessage by mutableStateOf<String?>(null)
        private set
    var isSaving by mutableStateOf(false)
        private set

    fun updatePrompt(text: String) {
        promptText = text
    }

    fun clearMessage() {
        saveMessage = null
    }

    fun generatePlan() {
        if (promptText.isBlank()) return

        viewModelScope.launch {
            uiState = MealUiState.Loading
            try {
                val user = userRepo.userData.first()
                val tdee = hitungTDEE(user)

                val finalPrompt = """
                    Profil User: Usia ${user.usia}, Berat ${user.berat}kg, Target $tdee kkal.
                    Request: $promptText.
                    Buatkan meal plan JSON lengkap.
                """.trimIndent()

                val result = repository.generateMealPlan(finalPrompt)

                if (result != null) {
                    uiState = MealUiState.Success(result)
                } else {
                    uiState = MealUiState.Error("Gagal mendapatkan rekomendasi.")
                }
            } catch (e: Exception) {
                uiState = MealUiState.Error("Error: ${e.message}")
            }
        }
    }

    fun savePlanToLog() {
        val currentState = uiState
        if (currentState !is MealUiState.Success) return

        viewModelScope.launch {
            isSaving = true
            try {
                val user = userRepo.userData.first()
                if (user.id == 0) {
                    saveMessage = "User belum login"
                    isSaving = false
                    return@launch
                }

                val today = LocalDate.now()
                val formatter = DateTimeFormatter.ISO_DATE
                val todayString = today.format(formatter)
                val checkResponse = makananRepo.getMakanan(user.id, todayString)
                val existingFood = checkResponse.body()?.data ?: emptyList()
                val targetDate = if (existingFood.isNotEmpty()) {
                    today.plusDays(1)
                } else {
                    today
                }
                val targetDateString = targetDate.format(formatter)

                var successCount = 0
                val mealList = currentState.data.meals

                mealList.forEach { mealAi ->
                    try {
                        // Log data yang akan disimpan untuk debugging
                        println("Saving meal: ${mealAi.nama}")
                        println("Data: kalori=${mealAi.kalori}, protein=${mealAi.protein}, karbo=${mealAi.karbo}, lemak=${mealAi.lemak}, serat=${mealAi.serat}")

                        val dataMakananBaru = DataMakanan(
                            id = 0,
                            user_id = user.id,
                            nama = mealAi.nama,
                            kategori = mapCategory(mealAi.kategori),
                            kalori = mealAi.kalori,
                            protein = mealAi.protein.toInt(),
                            karbo = mealAi.karbo.toInt(),
                            lemak = mealAi.lemak.toInt(),
                            serat = mealAi.serat.toInt(),
                            quantity = mealAi.quantity,
                            hari_tanggal = targetDateString
                        )

                        println("DataMakanan created successfully: $dataMakananBaru")

                        val saveResponse = makananRepo.addMakanan(dataMakananBaru)
                        if (saveResponse.isSuccessful) {
                            successCount++
                            val responseData = saveResponse.body()
                            println("✓ Berhasil save: ${mealAi.nama} - insertId: ${responseData?.data?.insertId}")
                        } else {
                            val errorBody = saveResponse.errorBody()?.string()
                            println("✗ Gagal Save: ${saveResponse.code()} - $errorBody")
                            println("Request data: $dataMakananBaru")
                        }
                    } catch (e: Exception) {
                        println("✗ Exception saat save ${mealAi.nama}: ${e.message}")
                        e.printStackTrace()
                    }
                }

                val dateLabel = if (targetDate == today) "Hari Ini" else "Besok"
                saveMessage = if (successCount == mealList.size) {
                    "Berhasil menyimpan plan untuk $dateLabel"
                } else {
                    "Tersimpan sebagian ($successCount/${mealList.size}) untuk $dateLabel"
                }

            } catch (e: Exception) {
                saveMessage = "Gagal menyimpan: ${e.message}"
                e.printStackTrace()
            } finally {
                isSaving = false
            }
        }
    }

    private fun mapCategory(aiCategory: String): String {
        return when {
            aiCategory.contains("Sarapan", true) -> "Sarapan"
            aiCategory.contains("Siang", true) -> "Makan Siang"
            aiCategory.contains("Malam", true) -> "Makan Malam"
            else -> "Snack"
        }
    }

    private fun hitungTDEE(user: DataUser): Int {
        if (user.berat == 0f) return 2000
        return (user.berat * 25).toInt()
    }
}