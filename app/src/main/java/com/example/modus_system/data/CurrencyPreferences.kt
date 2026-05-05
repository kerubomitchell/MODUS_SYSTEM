package com.example.modus_system.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class CurrencyPreferences(private val context: Context) {

    companion object {
        val CURRENCY_KEY = stringPreferencesKey("currency")
        val CURRENCIES = listOf("KES", "USD", "EUR", "GBP", "UGX", "TZS", "NGN", "ZAR")
    }

    val selectedCurrency: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[CURRENCY_KEY] ?: "KES"
    }

    suspend fun saveCurrency(currency: String) {
        context.dataStore.edit { preferences ->
            preferences[CURRENCY_KEY] = currency
        }
    }
}
