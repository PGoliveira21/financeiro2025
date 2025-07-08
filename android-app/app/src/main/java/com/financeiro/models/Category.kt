package com.financeiro.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categories")
data class Category(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val icon: String,
    val color: String,
    val type: TransactionType,
    val isCustom: Boolean = false
)

object DefaultCategories {
    val expenseCategories = listOf(
        Category(1, "Alimentação", "restaurant", "#FF9800", TransactionType.EXPENSE),
        Category(2, "Transporte", "directions_car", "#2196F3", TransactionType.EXPENSE),
        Category(3, "Moradia", "home", "#4CAF50", TransactionType.EXPENSE),
        Category(4, "Saúde", "local_hospital", "#F44336", TransactionType.EXPENSE),
        Category(5, "Educação", "school", "#9C27B0", TransactionType.EXPENSE),
        Category(6, "Lazer", "movie", "#E91E63", TransactionType.EXPENSE),
        Category(7, "Vestuário", "checkroom", "#795548", TransactionType.EXPENSE),
        Category(8, "Outros", "more_horiz", "#607D8B", TransactionType.EXPENSE)
    )
    
    val incomeCategories = listOf(
        Category(9, "Salário", "account_balance_wallet", "#4CAF50", TransactionType.INCOME),
        Category(10, "Freelance", "laptop", "#2196F3", TransactionType.INCOME),
        Category(11, "Investimentos", "trending_up", "#FF9800", TransactionType.INCOME),
        Category(12, "Outros", "more_horiz", "#607D8B", TransactionType.INCOME)
    )
}

