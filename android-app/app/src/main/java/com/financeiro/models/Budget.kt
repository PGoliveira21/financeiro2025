package com.financeiro.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "budgets")
data class Budget(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val categoryId: Long,
    val amount: Double,
    val month: Int,
    val year: Int
)

data class BudgetWithCategory(
    val budget: Budget,
    val category: Category,
    val spent: Double
) {
    val percentage: Double
        get() = (spent / budget.amount) * 100
        
    val status: BudgetStatus
        get() = when {
            percentage >= 100 -> BudgetStatus.EXCEEDED
            percentage >= 75 -> BudgetStatus.WARNING
            else -> BudgetStatus.SAFE
        }
}

enum class BudgetStatus {
    SAFE, WARNING, EXCEEDED
}

