package com.ifat.app.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val amount: Double,
    val type: String, // "INCOME" অথবা "EXPENSE"
    val note: String,
    val date: Long = System.currentTimeMillis()
)
