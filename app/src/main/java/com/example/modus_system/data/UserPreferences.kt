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