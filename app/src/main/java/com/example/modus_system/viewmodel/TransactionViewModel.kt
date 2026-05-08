package com.example.modus_system.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.modus_system.data.CurrencyPreferences
import com.example.modus_system.data.FirebaseAuthManager
import com.example.modus_system.data.Transaction
import com.example.modus_system.data.TransactionDatabase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Calendar

@OptIn(ExperimentalCoroutinesApi::class)
class TransactionViewModel(application: Application) : AndroidViewModel(application) {

    private val dao = TransactionDatabase.getDatabase(application).transactionDao()
    private val notificationDao = TransactionDatabase.getDatabase(application).notificationDao()
    private val currencyPreferences = CurrencyPreferences(application)
    private val userPreferences = com.example.modus_system.data.UserPreferences(application)
    private val authManager = FirebaseAuthManager()

    // Notifications
    val allNotifications: Flow<List<com.example.modus_system.data.Notification>> =
        notificationDao.getAllNotifications()

    fun getNotificationsByCategory(category: String): Flow<List<com.example.modus_system.data.Notification>> =
        notificationDao.getNotificationsByCategory(category)

    fun addNotification(title: String, message: String, category: String) {
        viewModelScope.launch {
            notificationDao.insertNotification(
                com.example.modus_system.data.Notification(
                    title = title,
                    message = message,
                    category = category
                )
            )
        }
    }

    fun markNotificationAsRead(id: Int) {
        viewModelScope.launch {
            notificationDao.markAsRead(id)
        }
    }

    fun deleteNotification(notification: com.example.modus_system.data.Notification) {
        viewModelScope.launch {
            notificationDao.deleteNotification(notification)
        }
    }

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
    
    // Leaky Shield Logic: Identify recurring merchant names in IRON_SHIELD
    val recurringShieldCosts: Flow<List<Pair<String, Double>>> = ironShieldTransactions.map { list ->
        list.groupBy { it.merchantName }
            .filter { it.value.size > 1 } // Appears more than once
            .map { entry ->
                // Sort by timestamp to check average gap? 
                // For now, let's just identify them as recurring.
                entry.key to entry.value.sumOf { it.amount } / entry.value.size // Average amount
            }
            .sortedByDescending { it.second }
    }

    val modusScore: Flow<Int> = combine(ironShieldTotal, goldenPathTotal) { iron, golden ->
        val ironVal = iron ?: 0.0
        val goldenVal = golden ?: 0.0
        val total = ironVal + goldenVal
        if (total > 0) {
            ((goldenVal / total) * 100).toInt()
        } else {
            0
        }
    }

    val ironShieldCount: Flow<Int> = dao.getCountByCategory("IRON_SHIELD")
    val goldenPathCount: Flow<Int> = dao.getCountByCategory("GOLDEN_PATH")

    val transactionsByDay: Flow<Map<Long, List<Transaction>>> = allTransactions.map { list ->
        list.groupBy { transaction ->
            val cal = Calendar.getInstance()
            cal.timeInMillis = transaction.timestamp
            cal.set(Calendar.HOUR_OF_DAY, 0)
            cal.set(Calendar.MINUTE, 0)
            cal.set(Calendar.SECOND, 0)
            cal.set(Calendar.MILLISECOND, 0)
            cal.timeInMillis
        }
    }

    val selectedCurrency = currencyPreferences.selectedCurrency
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "KES")

    val targetScore = userPreferences.targetScore
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 50)

    val userPhotoUri = userPreferences.userPhotoUri
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val userName = userPreferences.userName
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "")

    val userEmail = userPreferences.userEmail
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "")

    val scoreGap: Flow<Int> = combine(modusScore, targetScore) { current, target ->
        target - current
    }

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
            val transaction = Transaction(
                merchantName = merchantName,
                amount = amount,
                category = category,
                isEssential = isEssential,
                note = note,
                currency = currency
            )
            val id = dao.insertTransaction(transaction).toInt()
            
            // Sync to Firebase if logged in
            authManager.currentUser?.uid?.let { userId ->
                authManager.syncTransaction(userId, transaction.copy(id = id))
            }
        }
    }

    fun updateTransaction(transaction: Transaction) {
        viewModelScope.launch {
            dao.updateTransaction(transaction)
            
            // Sync to Firebase if logged in
            authManager.currentUser?.uid?.let { userId ->
                authManager.syncTransaction(userId, transaction)
            }
        }
    }

    fun deleteTransaction(transaction: Transaction) {
        viewModelScope.launch {
            dao.deleteTransaction(transaction)
            
            // Sync to Firebase if logged in
            authManager.currentUser?.uid?.let { userId ->
                authManager.deleteCloudTransaction(userId, transaction.id)
            }
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

    fun updateTargetScore(score: Int) {
        viewModelScope.launch {
            userPreferences.saveTargetScore(score)
        }
    }

    fun updateUserPhoto(uri: String) {
        viewModelScope.launch {
            userPreferences.saveUserPhotoUri(uri)
        }
    }
}