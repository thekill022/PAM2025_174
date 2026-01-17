package com.example.finalproject.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.preferencesDataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.finalproject.model.DataUser
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_session")

class UserPreferenceRepository(private val dataStore: DataStore<Preferences>) {

    private companion object {
        val TOKEN_KEY = stringPreferencesKey("token")
        val IS_LOGIN_KEY = booleanPreferencesKey("is_login")
        val ID_KEY = intPreferencesKey("id")
        val NAMA_KEY = stringPreferencesKey("nama")
        val EMAIL_KEY = stringPreferencesKey("email")
        val TINGGI_KEY = intPreferencesKey("tinggi")
        val BERAT_KEY = floatPreferencesKey("berat")
        val JENIS_KELAMIN_KEY = stringPreferencesKey("jenis_kelamin")
        val AKTIVITAS_KEY = stringPreferencesKey("aktivitas_harian")
        val USIA_KEY = intPreferencesKey("usia")
    }

    suspend fun saveSession(token: String, user: DataUser) {
        dataStore.edit { preferences ->
            preferences[TOKEN_KEY] = token
            preferences[IS_LOGIN_KEY] = true
            preferences[ID_KEY] = user.id
            preferences[NAMA_KEY] = user.nama
            preferences[EMAIL_KEY] = user.email
            preferences[TINGGI_KEY] = user.tinggi_badan
            preferences[BERAT_KEY] = user.berat
            preferences[JENIS_KELAMIN_KEY] = user.jenis_kelamin
            preferences[AKTIVITAS_KEY] = user.aktivitas_harian
            preferences[USIA_KEY] = user.usia
        }
    }

    val userToken: Flow<String?> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[TOKEN_KEY]
        }

    val isUserLogin: Flow<Boolean> = dataStore.data
        .map { preferences ->
            preferences[IS_LOGIN_KEY] ?: false
        }

    val userData: Flow<DataUser> = dataStore.data
        .map { preferences ->
            DataUser(
                id = preferences[ID_KEY] ?: 0,
                nama = preferences[NAMA_KEY] ?: "",
                email = preferences[EMAIL_KEY] ?: "",
                password = "",
                usia = preferences[USIA_KEY] ?: 0,
                tinggi_badan = preferences[TINGGI_KEY] ?: 0,
                aktivitas_harian = preferences[AKTIVITAS_KEY] ?: "",
                jenis_kelamin = preferences[JENIS_KELAMIN_KEY] ?: "",
                created_at = "",
                berat = preferences[BERAT_KEY] ?: 0.0f
            )
        }

    suspend fun logout() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}