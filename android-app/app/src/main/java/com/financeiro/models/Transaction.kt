package com.financeiro.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "transactions")
data class Transaction(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val amount: Double,
    val categoryId: Long,
    val description: String,
    val date: Date,
    val type: TransactionType,
    val createdAt: Date = Date()
)

enum class TransactionType {
    INCOME, EXPENSE
}

