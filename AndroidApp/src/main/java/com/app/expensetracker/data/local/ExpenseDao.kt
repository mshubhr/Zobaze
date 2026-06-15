package com.app.expensetracker.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.app.expensetracker.data.model.Expense

@Dao
interface ExpenseDao {
    @Query("SELECT * FROM expenses ORDER BY timestamp DESC")
    suspend fun getAll(): List<Expense>

    @Query("SELECT * FROM expenses WHERE date(timestamp/1000, 'unixepoch') = date(:ts/1000, 'unixepoch') ORDER BY timestamp DESC")
    suspend fun getForDate(ts: Long): List<Expense>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(expense: Expense)

    @Delete
    suspend fun delete(expense: Expense)

    @Query("SELECT * FROM expenses WHERE isSynced = 0")
    suspend fun getPending(): List<Expense>

    @Update
    suspend fun update(expense: Expense)

}