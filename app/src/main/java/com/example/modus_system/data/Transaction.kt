package com.example.modus_system.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class Transaction(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo(name = "merchant_name")
    val merchantName: String,

    @ColumnInfo(name = "amount")
    val amount: Double,

    @ColumnInfo(name = "category")
    val category: String, // "IRON_SHIELD" or "GOLDEN_PATH"

    @ColumnInfo(name = "currency")
    val currency: String = "KES",

    @ColumnInfo(name = "note")
    val note: String = "",

    @ColumnInfo(name = "timestamp")
    val timestamp: Long = System.currentTimeMillis(),

    @ColumnInfo(name = "is_essential")
    val isEssential: Boolean = true
)
