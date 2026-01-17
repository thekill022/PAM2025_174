package com.example.finalproject.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.finalproject.model.DataMakanan
import com.example.finalproject.model.DataUser
import com.example.finalproject.repository.RepositoryDataMakanan
import com.example.finalproject.repository.UserPreferenceRepository
import com.example.finalproject.model.FoodCalorieData
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
    val errorMessage: String? = null,
    // State untuk dialog input makanan
    val showAddFoodDialog: Boolean = false,
    val selectedCategory: String = "Sarapan",
    val foodInputText: String = "",
    val isLoadingCalories: Boolean = false,
    val calorieData: FoodCalorieData? = null,
    val addFoodMessage: String? = null
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
            println("📅 LoadData - Current date: ${_currentDate.value}, Formatted: $dateString")

            val user = userRepo.userData.first()
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

    fun refreshData() {
        viewModelScope.launch {
            val dateString = _currentDate.value.format(apiFormatter)
            println("🔄 RefreshData - Current date: ${_currentDate.value}, Formatted: $dateString")
            val user = userRepo.userData.first()
            
            if (user.id != 0) {
                fetchMakananHarian(user.id, dateString)
            }
        }
    }


    private fun fetchMakananHarian(userId: Int, date: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                println("🔍 Fetching makanan for userId: $userId, date: $date")
                val response = makananRepo.getMakanan(userId, date)

                println("📡 Response code: ${response.code()}")
                println("📡 Response successful: ${response.isSuccessful}")

                if (response.isSuccessful) {
                    val listMakanan = response.body()?.data ?: emptyList()
                    println("✅ Data received: ${listMakanan.size} items")
                    listMakanan.forEach { makanan ->
                        println("   - ${makanan.nama} (${makanan.kategori}) - ${makanan.hari_tanggal}")
                    }
                    processFoodData(listMakanan)
                } else {
                    val errorBody = response.errorBody()?.string()
                    println("❌ Error response: $errorBody")
                    processFoodData(emptyList())
                }
            } catch (e: Exception) {
                println("❌ Exception: ${e.message}")
                e.printStackTrace()
                _uiState.update { it.copy(errorMessage = "Error: ${e.message}") }
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }


    fun changeDate(daysToAdd: Long) {
        _currentDate.value = _currentDate.value.plusDays(daysToAdd)
        refreshData()
    }

    fun getDateString(): String {
        return if (_currentDate.value.isEqual(LocalDate.now())) {
            "Hari Ini"
        } else {
            _currentDate.value.format(displayFormatter)
        }
    }

    private fun processFoodData(list: List<DataMakanan>) {
        val totalKalori = list.sumOf { it.kalori }
        val totalProtein = list.sumOf { it.protein }
        val totalKarbo = list.sumOf { it.karbo}
        val totalLemak = list.sumOf { it.lemak}

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

    // ===== Fungsi untuk Manual Food Input =====
    
    fun showAddFoodDialog(category: String) {
        _uiState.update {
            it.copy(
                showAddFoodDialog = true,
                selectedCategory = category,
                foodInputText = "",
                calorieData = null,
                addFoodMessage = null
            )
        }
    }

    fun hideAddFoodDialog() {
        _uiState.update {
            it.copy(
                showAddFoodDialog = false,
                foodInputText = "",
                calorieData = null,
                addFoodMessage = null
            )
        }
    }

    fun updateFoodInputText(text: String) {
        _uiState.update { it.copy(foodInputText = text) }
    }

    fun getFoodCalories() {
        val foodText = _uiState.value.foodInputText.trim()
        if (foodText.isBlank()) {
            _uiState.update { it.copy(addFoodMessage = "Masukkan nama makanan") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingCalories = true, addFoodMessage = null) }
            try {
                val response = makananRepo.getFoodCalories(foodText)
                
                if (response.isSuccessful) {
                    val calorieData = response.body()?.data
                    if (calorieData != null) {
                        _uiState.update {
                            it.copy(
                                calorieData = calorieData,
                                isLoadingCalories = false,
                                addFoodMessage = "Data kalori berhasil didapat!"
                            )
                        }
                    } else {
                        _uiState.update {
                            it.copy(
                                isLoadingCalories = false,
                                addFoodMessage = "Gagal mendapatkan data kalori"
                            )
                        }
                    }
                } else {
                    _uiState.update {
                        it.copy(
                            isLoadingCalories = false,
                            addFoodMessage = "Error: ${response.code()}"
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoadingCalories = false,
                        addFoodMessage = "Error: ${e.message}"
                    )
                }
            }
        }
    }

    fun saveFoodFromCalorieData() {
        val calorieData = _uiState.value.calorieData
        val category = _uiState.value.selectedCategory
        
        if (calorieData == null) {
            _uiState.update { it.copy(addFoodMessage = "Tidak ada data untuk disimpan") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingCalories = true) }
            try {
                val user = userRepo.userData.first()
                if (user.id == 0) {
                    _uiState.update {
                        it.copy(
                            isLoadingCalories = false,
                            addFoodMessage = "User belum login"
                        )
                    }
                    return@launch
                }

                val dateString = _currentDate.value.format(apiFormatter)
                println("💾 Saving food for date: $dateString")
                var successCount = 0

                // Simpan setiap food item dari AI
                calorieData.foodItems.forEach { foodItem ->
                    try {
                        val dataMakanan = DataMakanan(
                            id = 0,
                            user_id = user.id,
                            nama = foodItem.name,
                            kategori = category,
                            kalori = foodItem.calories,
                            protein = foodItem.protein.toInt(),
                            karbo = foodItem.carbohydrates.toInt(),
                            lemak = foodItem.fat.toInt(),
                            serat = foodItem.fiber.toInt(),
                            quantity = foodItem.quantity,
                            hari_tanggal = dateString
                        )

                        println("💾 Saving: ${dataMakanan.nama} with date: ${dataMakanan.hari_tanggal}")

                        val saveResponse = makananRepo.addMakanan(dataMakanan)
                        if (saveResponse.isSuccessful) {
                            successCount++
                            println("✓ Berhasil save: ${foodItem.name}")
                        } else {
                            println("✗ Gagal save: ${foodItem.name} - ${saveResponse.code()}")
                        }
                    } catch (e: Exception) {
                        println("✗ Exception save ${foodItem.name}: ${e.message}")
                    }
                }

                if (successCount > 0) {
                    _uiState.update {
                        it.copy(
                            isLoadingCalories = false,
                            addFoodMessage = "Berhasil menyimpan $successCount makanan",
                            showAddFoodDialog = false
                        )
                    }
                    // Refresh data
                    refreshData()
                } else {
                    _uiState.update {
                        it.copy(
                            isLoadingCalories = false,
                            addFoodMessage = "Gagal menyimpan makanan"
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoadingCalories = false,
                        addFoodMessage = "Error: ${e.message}"
                    )
                }
            }
        }
    }
}