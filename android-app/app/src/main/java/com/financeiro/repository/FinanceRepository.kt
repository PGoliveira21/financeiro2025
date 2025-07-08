package com.financeiro.repository

import com.financeiro.database.*
import com.financeiro.models.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import java.util.*

class FinanceRepository(
    private val transactionDao: TransactionDao,
    private val categoryDao: CategoryDao,
    private val budgetDao: BudgetDao
) {
    
    // Transações
    fun getAllTransactions(): Flow<List<Transaction>> = transactionDao.getAllTransactions()
    
    suspend fun insertTransaction(transaction: Transaction): Long {
        return transactionDao.insertTransaction(transaction)
    }
    
    suspend fun deleteTransaction(transaction: Transaction) {
        transactionDao.deleteTransaction(transaction)
    }
    
    suspend fun getBalance(): Double {
        return transactionDao.getBalance() ?: 0.0
    }
    
    suspend fun getTotalIncome(): Double {
        return transactionDao.getTotalIncome() ?: 0.0
    }
    
    suspend fun getTotalExpenses(): Double {
        return transactionDao.getTotalExpenses() ?: 0.0
    }
    
    // Categorias
    fun getAllCategories(): Flow<List<Category>> = categoryDao.getAllCategories()
    
    fun getCategoriesByType(type: TransactionType): Flow<List<Category>> = 
        categoryDao.getCategoriesByType(type)
    
    suspend fun getCategoryById(id: Long): Category? = categoryDao.getCategoryById(id)
    
    suspend fun insertCategory(category: Category): Long {
        return categoryDao.insertCategory(category)
    }
    
    // Orçamentos
    fun getAllBudgets(): Flow<List<Budget>> = budgetDao.getAllBudgets()
    
    fun getBudgetsByMonth(month: Int, year: Int): Flow<List<Budget>> = 
        budgetDao.getBudgetsByMonth(month, year)
    
    suspend fun insertBudget(budget: Budget): Long {
        return budgetDao.insertBudget(budget)
    }
    
    suspend fun deleteBudget(budget: Budget) {
        budgetDao.deleteBudget(budget)
    }
    
    // Orçamentos com categorias e gastos
    fun getBudgetsWithCategoryAndSpent(month: Int, year: Int): Flow<List<BudgetWithCategory>> {
        return combine(
            budgetDao.getBudgetsByMonth(month, year),
            categoryDao.getAllCategories()
        ) { budgets, categories ->
            budgets.mapNotNull { budget ->
                val category = categories.find { it.id == budget.categoryId }
                if (category != null) {
                    val spent = getSpentByCategory(budget.categoryId, month, year)
                    BudgetWithCategory(budget, category, spent)
                } else null
            }
        }
    }
    
    private suspend fun getSpentByCategory(categoryId: Long, month: Int, year: Int): Double {
        val monthStr = String.format("%02d", month)
        val yearStr = year.toString()
        val transactions = transactionDao.getExpensesByCategoryAndMonth(categoryId, monthStr, yearStr)
        return transactions.sumOf { it.amount }
    }
    
    // Estatísticas por categoria
    suspend fun getExpensesByCategory(): Map<Category, Double> {
        val categories = categoryDao.getCategoriesByType(TransactionType.EXPENSE)
        val transactions = transactionDao.getAllTransactions()
        
        val result = mutableMapOf<Category, Double>()
        
        // Esta é uma implementação simplificada
        // Na implementação real, você usaria combine() para observar mudanças
        return result
    }
    
    // Exportação de dados
    suspend fun exportTransactionsToCSV(): String {
        val transactions = transactionDao.getAllTransactions()
        val categories = categoryDao.getAllCategories()
        
        // Esta é uma implementação simplificada
        // Na implementação real, você combinaria os flows e geraria o CSV
        val header = "Data,Categoria,Descrição,Valor,Tipo\\n"
        
        return header // + dados das transações
    }
}

