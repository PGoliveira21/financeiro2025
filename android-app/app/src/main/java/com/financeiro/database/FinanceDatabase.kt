package com.financeiro.database

import androidx.room.*
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.financeiro.models.*
import java.util.Date

@Database(
    entities = [Transaction::class, Category::class, Budget::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class FinanceDatabase : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao
    abstract fun categoryDao(): CategoryDao
    abstract fun budgetDao(): BudgetDao
    
    companion object {
        @Volatile
        private var INSTANCE: FinanceDatabase? = null
        
        fun getDatabase(context: android.content.Context): FinanceDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    FinanceDatabase::class.java,
                    "finance_database"
                )
                .addCallback(object : RoomDatabase.Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        // Inserir categorias padrão
                        insertDefaultCategories(db)
                    }
                })
                .build()
                INSTANCE = instance
                instance
            }
        }
        
        private fun insertDefaultCategories(db: SupportSQLiteDatabase) {
            // Inserir categorias de gastos
            DefaultCategories.expenseCategories.forEach { category ->
                db.execSQL(
                    "INSERT INTO categories (id, name, icon, color, type, isCustom) VALUES (?, ?, ?, ?, ?, ?)",
                    arrayOf(category.id, category.name, category.icon, category.color, category.type.name, false)
                )
            }
            
            // Inserir categorias de receitas
            DefaultCategories.incomeCategories.forEach { category ->
                db.execSQL(
                    "INSERT INTO categories (id, name, icon, color, type, isCustom) VALUES (?, ?, ?, ?, ?, ?)",
                    arrayOf(category.id, category.name, category.icon, category.color, category.type.name, false)
                )
            }
        }
    }
}

class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }
    
    @TypeConverter
    fun fromTransactionType(type: TransactionType): String {
        return type.name
    }
    
    @TypeConverter
    fun toTransactionType(type: String): TransactionType {
        return TransactionType.valueOf(type)
    }
}

