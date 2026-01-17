package com.example.finalproject.viewmodel

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.finalproject.repository.AplikasiCalori

fun CreationExtras.aplikasiCalori(): AplikasiCalori = (
        this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as AplikasiCalori
        )

object PenyediaViewModel {
    val Factory = viewModelFactory {
        initializer {
            LoginViewModel(
                aplikasiCalori().container.repositoryDataUser,
                aplikasiCalori().container.repositoryDataBerat
            )
        }
        initializer {
            RegisterViewModel(
                aplikasiCalori().container.repositoryDataUser
            )
        }
        initializer {
            HomeViewModel(
                aplikasiCalori().container.userPreferenceRepository,
                aplikasiCalori().container.repositoryDataMakanan
            )
        }
        initializer {
            MainViewModel(
                aplikasiCalori().container.userPreferenceRepository
            )
        }
    }
}