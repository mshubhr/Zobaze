package com.app.expensetracker.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.app.expensetracker.data.model.Expense

@Database(
    entities = [Expense::class],
    version = 2,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun expenseDao(): ExpenseDao
}