package com.example.modus_system.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserPreferences(private val context: Context) {

    companion object {
        val USER_NAME = stringPreferencesKey("user_name")
        val USER_EMAIL = stringPreferencesKey("user_email")
        val USER_PHONE = stringPreferencesKey("user_phone")
        val USER_PASSWORD = stringPreferencesKey("user_password")
        val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
        val IS_REGISTERED = booleanPreferencesKey("is_registered")
        val TARGET_SCORE = stringPreferencesKey("target_score") // Stored as string to handle empty/default
        val USER_PHOTO_URI = stringPreferencesKey("user_photo_uri")
    }

    val userPhotoUri: Flow<String?> = context.dataStore.data.map {
        it[USER_PHOTO_URI]
    }

    suspend fun saveUserPhotoUri(uri: String) {
        context.dataStore.edit {
            it[USER_PHOTO_URI] = uri
        }
    }

    val targetScore: Flow<Int> = context.dataStore.data.map {
        it[TARGET_SCORE]?.toIntOrNull() ?: 50
    }

    suspend fun saveTargetScore(score: Int) {
        context.dataStore.edit {
            it[TARGET_SCORE] = score.toString()
        }
    }

    val userName: Flow<String> = context.dataStore.data.map {
        it[USER_NAME] ?: ""
    }
    val userEmail: Flow<String> = context.dataStore.data.map {
        it[USER_EMAIL] ?: ""
    }
    val userPhone: Flow<String> = context.dataStore.data.map {
        it[USER_PHONE] ?: ""
    }
    val isLoggedIn: Flow<Boolean> = context.dataStore.data.map {
        it[IS_LOGGED_IN] ?: false
    }
    val isRegistered: Flow<Boolean> = context.dataStore.data.map {
        it[IS_REGISTERED] ?: false
    }

    suspend fun saveUser(
        name: String,
        email: String,
        phone: String,
        password: String
    ) {
        context.dataStore.edit {
            it[USER_NAME] = name
            it[USER_EMAIL] = email
            it[USER_PHONE] = phone
            it[USER_PASSWORD] = password
            it[IS_REGISTERED] = true
            it[IS_LOGGED_IN] = true
        }
    }

    suspend fun setLoggedIn(loggedIn: Boolean) {
        context.dataStore.edit {
            it[IS_LOGGED_IN] = loggedIn
        }
    }

    suspend fun saveProfile(name: String, email: String, phone: String) {
        context.dataStore.edit {
            it[USER_NAME] = name
            it[USER_EMAIL] = email
            it[USER_PHONE] = phone
            it[IS_REGISTERED] = true
        }
    }

    suspend fun login(email: String, password: String): Boolean {
        var success = false
        context.dataStore.edit { prefs ->
            val savedEmail = prefs[USER_EMAIL] ?: ""
            val savedPassword = prefs[USER_PASSWORD] ?: ""
            if (email == savedEmail && password == savedPassword) {
                prefs[IS_LOGGED_IN] = true
                success = true
            }
        }
        return success
    }

    suspend fun logout() {
        context.dataStore.edit {
            it[IS_LOGGED_IN] = false
        }
    }
}