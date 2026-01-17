package com.example.finalproject.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.finalproject.repository.UserPreferenceRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

data class MainUiState(
    val isLoggedIn: Boolean = false,
    val isLoading: Boolean = true
)

class MainViewModel(private val repository: UserPreferenceRepository) : ViewModel() {
    val uiState: StateFlow<MainUiState> = repository.isUserLogin
        .map { isLogin ->
            MainUiState(isLoggedIn = isLogin, isLoading = false)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = MainUiState(isLoading = true)
        )
}