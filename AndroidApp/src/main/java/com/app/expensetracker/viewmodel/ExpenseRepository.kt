package com.app.expensetracker.viewmodel

import com.app.expensetracker.data.local.ExpenseDao
import com.app.expensetracker.data.model.Expense
import kotlinx.coroutines.delay
import java.util.Calendar

class ExpenseRepository(private val dao: ExpenseDao? = null) {

    private val memory = mutableListOf<Expense>()

    suspend fun addExpense(e: Expense) {
        memory.add(0, e)
        dao?.insert(e.copy(isSynced = false))
    }

    suspend fun syncPending() {
        delay(2000)
        dao?.getPending()?.forEach {
            dao.update(it.copy(isSynced = true))
        }
    }

    suspend fun getExpensesForDate(ts: Long): List<Expense> {
        return dao?.getForDate(ts) ?: memory.filter {
            val cal = Calendar.getInstance()
            cal.timeInMillis = it.timestamp
            val cal2 = Calendar.getInstance()
            cal2.timeInMillis = ts
            cal.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) && cal.get(Calendar.DAY_OF_YEAR) == cal2.get(
                Calendar.DAY_OF_YEAR
            )
        }.sortedByDescending { it.timestamp }
    }

    suspend fun getRecent(n: Int = 100): List<Expense> {
        return dao?.getAll() ?: memory.sortedByDescending { it.timestamp }.take(n)
    }
}