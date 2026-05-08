package com.example.modus_system.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {

    @Insert
    suspend fun insertTransaction(transaction: Transaction): Long

    @Update
    suspend fun updateTransaction(transaction: Transaction)

    @Delete
    suspend fun deleteTransaction(transaction: Transaction)

    @Query("SELECT * FROM transactions ORDER BY timestamp DESC")
    fun getAllTransactions(): Flow<List<Transaction>>

    @Query("SELECT * FROM transactions WHERE category = :category ORDER BY timestamp DESC")
    fun getTransactionsByCategory(category: String): Flow<List<Transaction>>

    @Query("""
        SELECT * FROM transactions 
        WHERE category = :category 
        AND (merchant_name LIKE '%' || :query || '%' 
        OR note LIKE '%' || :query || '%')
        ORDER BY timestamp DESC
    """)
    fun searchTransactionsByCategory(
        category: String,
        query: String
    ): Flow<List<Transaction>>

    @Query("""
        SELECT * FROM transactions 
        WHERE merchant_name LIKE '%' || :query || '%' 
        OR note LIKE '%' || :query || '%'
        ORDER BY timestamp DESC
    """)
    fun searchAllTransactions(query: String): Flow<List<Transaction>>

    @Query("SELECT SUM(amount) FROM transactions WHERE category = :category")
    fun getTotalByCategory(category: String): Flow<Double?>

    @Query("SELECT COUNT(*) FROM transactions WHERE category = :category")
    fun getCountByCategory(category: String): Flow<Int>

    @Query("SELECT * FROM transactions WHERE id = :id")
    suspend fun getTransactionById(id: Int): Transaction?

    @Query("DELETE FROM transactions")
    suspend fun deleteAllTransactions()
}