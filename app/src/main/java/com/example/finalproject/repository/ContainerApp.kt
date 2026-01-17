package com.example.finalproject.repository

import android.app.Application
import android.content.Context // Pastikan import ini ada
import com.example.finalproject.apiservice.ServiceApiBerat
import com.example.finalproject.apiservice.ServiceApiMakanan
import com.example.finalproject.apiservice.ServiceApiMeal
import com.example.finalproject.apiservice.ServiceApiUsers
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit

interface ContainerApp {
    val repositoryDataUser : RepositoryDataUser
    val repositoryDataMakanan : RepositoryDataMakanan
    val repositoryDataBerat : RepositoryDataBerat
    val userPreferenceRepository : UserPreferenceRepository
    val repositoryMealPlan : RepositoryMealPlan
}

class DefaultContainerApp(private val context: Context) : ContainerApp {

    private val baseUrl = "http://10.0.2.2:4000/api/v1/"

    val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    val klien = OkHttpClient.Builder()
        .addInterceptor(logging)
        .build()

    private val retrofit : Retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(Json {
            ignoreUnknownKeys = true
            prettyPrint = true
            isLenient = true
        }.asConverterFactory("application/json".toMediaType()))
        .client(klien)
        .build()

    private val retrofitServiceUser : ServiceApiUsers by lazy {
        retrofit.create(ServiceApiUsers::class.java)
    }

    private val retrofitServiceMakanan : ServiceApiMakanan by lazy {
        retrofit.create(ServiceApiMakanan::class.java)
    }

    private val retrofitServiceBerat : ServiceApiBerat by lazy {
        retrofit.create(ServiceApiBerat::class.java)
    }

    private val retrofitServiceMealPlan : ServiceApiMeal by lazy {
        retrofit.create(ServiceApiMeal::class.java)
    }

    override val userPreferenceRepository = UserPreferenceRepository(context.dataStore)

    override val repositoryDataUser: RepositoryDataUser by lazy {
        JaringanRepositoryDataUser(retrofitServiceUser, userPreferenceRepository)
    }

    override val repositoryDataMakanan: RepositoryDataMakanan by lazy {
        JaringanRepositoryMakanan(retrofitServiceMakanan, userPreferenceRepository)
    }

    override val repositoryDataBerat: RepositoryDataBerat by lazy {
        JaringanRepositoryBerat(userPreferenceRepository, retrofitServiceBerat)
    }

    override val repositoryMealPlan: RepositoryMealPlan by lazy {
        JaringanRepositoryMealPlan(retrofitServiceMealPlan, userPreferenceRepository)
    }
}

class AplikasiCalori : Application() {
    lateinit var container : ContainerApp
    override fun onCreate() {
        super.onCreate()
        this.container = DefaultContainerApp(this)
    }
}