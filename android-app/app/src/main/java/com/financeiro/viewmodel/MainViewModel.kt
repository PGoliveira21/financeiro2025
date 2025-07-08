package com.financeiro.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.financeiro.database.FinanceDatabase
import com.financeiro.models.Transaction
import com.financeiro.models.TransactionType
import com.financeiro.repository.FinanceRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*

class MainViewModel(application: Application) : AndroidViewModel(application) {
    
    private val database = FinanceDatabase.getDatabase(application)
    private val repository = FinanceRepository(
        database.transactionDao(),
        database.categoryDao(),
        database.budgetDao()
    )
    
    // Estados observáveis
    val transactions = repository.getAllTransactions()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    
    private val _balance = MutableStateFlow(0.0)
    val balance: StateFlow<Double> = _balance.asStateFlow()
    
    private val _showAddTransactionDialog = MutableStateFlow(false)
    val showAddTransactionDialog: StateFlow<Boolean> = _showAddTransactionDialog.asStateFlow()
    
    private val _totalIncome = MutableStateFlow(0.0)
    val totalIncome: StateFlow<Double> = _totalIncome.asStateFlow()
    
    private val _totalExpenses = MutableStateFlow(0.0)
    val totalExpenses: StateFlow<Double> = _totalExpenses.asStateFlow()
    
    init {
        // Atualizar saldo quando as transações mudarem
        viewModelScope.launch {
            transactions.collect {
                updateBalance()
                updateTotals()
            }
        }
    }
    
    private suspend fun updateBalance() {
        _balance.value = repository.getBalance()
    }
    
    private suspend fun updateTotals() {
        _totalIncome.value = repository.getTotalIncome()
        _totalExpenses.value = repository.getTotalExpenses()
    }
    
    fun addTransaction(amount: Double, categoryId: Long, description: String, type: TransactionType) {
        viewModelScope.launch {
            val transaction = Transaction(
                amount = amount,
                categoryId = categoryId,
                description = description,
                date = Date(),
                type = type
            )
            repository.insertTransaction(transaction)
        }
    }
    
    fun deleteTransaction(transaction: Transaction) {
        viewModelScope.launch {
            repository.deleteTransaction(transaction)
        }
    }
    
    fun showAddTransactionDialog() {
        _showAddTransactionDialog.value = true
    }
    
    fun hideAddTransactionDialog() {
        _showAddTransactionDialog.value = false
    }
    
    fun exportData() {
        viewModelScope.launch {
            try {
                val csvData = repository.exportTransactionsToCSV()
                // Aqui você implementaria a lógica para salvar o arquivo
                // Por exemplo, usando o Storage Access Framework
                
                // Para demonstração, apenas log
                println("Dados exportados: $csvData")
            } catch (e: Exception) {
                // Tratar erro de exportação
                println("Erro ao exportar dados: ${e.message}")
            }
        }
    }
    
    // Funções para orçamentos
    fun addBudget(categoryId: Long, amount: Double, month: Int, year: Int) {
        viewModelScope.launch {
            val budget = com.financeiro.models.Budget(
                categoryId = categoryId,
                amount = amount,
                month = month,
                year = year
            )
            repository.insertBudget(budget)
        }
    }
    
    fun getBudgetsForCurrentMonth(): Flow<List<com.financeiro.models.BudgetWithCategory>> {
        val calendar = Calendar.getInstance()
        val month = calendar.get(Calendar.MONTH) + 1
        val year = calendar.get(Calendar.YEAR)
        return repository.getBudgetsWithCategoryAndSpent(month, year)
    }
    
    // Funções para estatísticas
    fun getExpensesByCategory(): Flow<Map<com.financeiro.models.Category, Double>> {
        // Implementar lógica para agrupar gastos por categoria
        return flowOf(emptyMap())
    }
}

