package com.example.modus_system.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.modus_system.data.CurrencyPreferences
import com.example.modus_system.data.Transaction
import com.example.modus_system.data.TransactionDatabase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class TransactionViewModel(application: Application) : AndroidViewModel(application) {

    private val dao = TransactionDatabase.getDatabase(application).transactionDao()
    private val currencyPreferences = CurrencyPreferences(application)

    // Search queries
    private val _ironShieldSearchQuery = MutableStateFlow("")
    val ironShieldSearchQuery: StateFlow<String> = _ironShieldSearchQuery

    private val _goldenPathSearchQuery = MutableStateFlow("")
    val goldenPathSearchQuery: StateFlow<String> = _goldenPathSearchQuery

    private val _homeSearchQuery = MutableStateFlow("")
    val homeSearchQuery: StateFlow<String> = _homeSearchQuery

    // Transactions
    val allTransactions: Flow<List<Transaction>> = _homeSearchQuery.flatMapLatest { query ->
        if (query.isBlank()) dao.getAllTransactions()
        else dao.searchAllTransactions(query)
    }

    val ironShieldTransactions: Flow<List<Transaction>> =
        _ironShieldSearchQuery.flatMapLatest { query ->
            if (query.isBlank()) dao.getTransactionsByCategory("IRON_SHIELD")
            else dao.searchTransactionsByCategory("IRON_SHIELD", query)
        }

    val goldenPathTransactions: Flow<List<Transaction>> =
        _goldenPathSearchQuery.flatMapLatest { query ->
            if (query.isBlank()) dao.getTransactionsByCategory("GOLDEN_PATH")
            else dao.searchTransactionsByCategory("GOLDEN_PATH", query)
        }

    val ironShieldTotal: Flow<Double?> = dao.getTotalByCategory("IRON_SHIELD")
    val goldenPathTotal: Flow<Double?> = dao.getTotalByCategory("GOLDEN_PATH")
    val ironShieldCount: Flow<Int> = dao.getCountByCategory("IRON_SHIELD")
    val goldenPathCount: Flow<Int> = dao.getCountByCategory("GOLDEN_PATH")

    val selectedCurrency = currencyPreferences.selectedCurrency
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "KES")

    // Search functions
    fun updateIronShieldSearch(query: String) {
        _ironShieldSearchQuery.value = query
    }

    fun updateGoldenPathSearch(query: String) {
        _goldenPathSearchQuery.value = query
    }

    fun updateHomeSearch(query: String) {
        _homeSearchQuery.value = query
    }

    // CRUD functions
    fun addTransaction(
        merchantName: String,
        amount: Double,
        category: String,
        isEssential: Boolean,
        note: String = "",
        currency: String = "KES"
    ) {
        viewModelScope.launch {
            dao.insertTransaction(
                Transaction(
                    merchantName = merchantName,
                    amount = amount,
                    category = category,
                    isEssential = isEssential,
                    note = note,
                    currency = currency
                )
            )
        }
    }

    fun updateTransaction(transaction: Transaction) {
        viewModelScope.launch {
            dao.updateTransaction(transaction)
        }
    }

    fun deleteTransaction(transaction: Transaction) {
        viewModelScope.launch {
            dao.deleteTransaction(transaction)
        }
    }

    suspend fun getTransactionById(id: Int): Transaction? {
        return dao.getTransactionById(id)
    }

    fun updateCurrency(currency: String) {
        viewModelScope.launch {
            currencyPreferences.saveCurrency(currency)
        }
    }
}