package com.example.finalproject.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.finalproject.model.DataMakanan
import com.example.finalproject.model.DataUser
import com.example.finalproject.repository.RepositoryDataMakanan
import com.example.finalproject.repository.UserPreferenceRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

data class HomeUiState(
    val nama: String = "",
    val targetKalori: Int = 2000,
    val kaloriTerisi: Int = 0,
    val protein: Int = 0,
    val karbo: Int = 0,
    val lemak: Int = 0,
    val listSarapan: List<DataMakanan> = emptyList(),
    val listSiang: List<DataMakanan> = emptyList(),
    val listMalam: List<DataMakanan> = emptyList(),
    val listSnack: List<DataMakanan> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class HomeViewModel(
    private val userRepo: UserPreferenceRepository,
    private val makananRepo: RepositoryDataMakanan
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val _currentDate = MutableStateFlow(LocalDate.now())
    private val apiFormatter = DateTimeFormatter.ISO_DATE
    private val displayFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy", Locale("id", "ID"))

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            val dateString = _currentDate.value.format(apiFormatter)

            userRepo.userData.collect { user ->
                _uiState.update {
                    it.copy(
                        nama = user.nama,
                        targetKalori = hitungTDEE(user)
                    )
                }

                if (user.id != 0) {
                    fetchMakananHarian(user.id, dateString)
                }
            }
        }
    }

    private fun fetchMakananHarian(userId: Int, date: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val response = makananRepo.getMakanan(userId, date)

                if (response.isSuccessful) {
                    val listMakanan = response.body()?.data ?: emptyList()
                    processFoodData(listMakanan)
                } else {
                    processFoodData(emptyList())
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = "Error: ${e.message}") }
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun changeDate(daysToAdd: Long) {
        _currentDate.value = _currentDate.value.plusDays(daysToAdd)
        loadData()
    }

    fun getDateString(): String {
        return if (_currentDate.value.isEqual(LocalDate.now())) {
            "Hari Ini"
        } else {
            _currentDate.value.format(displayFormatter)
        }
    }

    private fun processFoodData(list: List<DataMakanan>) {
        val totalKalori = list.sumOf { it.kalori * it.quantity }
        val totalProtein = list.sumOf { it.protein * it.quantity }
        val totalKarbo = list.sumOf { it.karbo * it.quantity }
        val totalLemak = list.sumOf { it.lemak * it.quantity }

        _uiState.update { state ->
            state.copy(
                kaloriTerisi = totalKalori,
                protein = totalProtein,
                karbo = totalKarbo,
                lemak = totalLemak,

                listSarapan = list.filter { it.kategori.equals("Sarapan", ignoreCase = true) },
                listSiang = list.filter { it.kategori.equals("Makan Siang", ignoreCase = true) },
                listMalam = list.filter { it.kategori.equals("Makan Malam", ignoreCase = true) },
                listSnack = list.filter { it.kategori.equals("Snack", ignoreCase = true) }
            )
        }
    }

    private fun hitungTDEE(user: DataUser): Int {
        if (user.berat == 0f || user.tinggi_badan == 0 || user.usia == 0) return 2000

        val bmr = if (user.jenis_kelamin == "laki-laki") {
            (10 * user.berat) + (6.25 * user.tinggi_badan) - (5 * user.usia) + 5
        } else {
            (10 * user.berat) + (6.25 * user.tinggi_badan) - (5 * user.usia) - 161
        }

        val multiplier = when {
            user.aktivitas_harian.contains("Sedentary", true) -> 1.2
            user.aktivitas_harian.contains("Ringan", true) -> 1.375
            user.aktivitas_harian.contains("Sedang", true) -> 1.55
            user.aktivitas_harian.contains("Berat", true) -> 1.725
            user.aktivitas_harian.contains("Ekstrem", true) -> 1.9
            else -> 1.2
        }

        return (bmr * multiplier).toInt()
    }
}