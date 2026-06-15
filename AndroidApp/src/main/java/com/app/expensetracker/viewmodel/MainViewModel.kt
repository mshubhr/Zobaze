package com.app.expensetracker.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.expensetracker.data.model.Expense
import com.app.expensetracker.data.model.ExpenseUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MainViewModel(private val repo: ExpenseRepository) : ViewModel() {
    private val _darkTheme = MutableStateFlow(false)
    val darkTheme: StateFlow<Boolean> = _darkTheme

    fun toggleTheme() {
        _darkTheme.value = !_darkTheme.value
    }

    private val _uiState = MutableStateFlow(ExpenseUiState())
    val uiState: StateFlow<ExpenseUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch { loadToday() }
    }

    private suspend fun loadToday() {
        val list = repo.getExpensesForDate(System.currentTimeMillis())
        _uiState.update { it.copy(expenses = list, totalSpentToday = list.sumOf { e -> e.amount }) }
    }

    fun onAddExpense(
        title: String, amountText: String, category: String, notes: String?, receiptUri: String?
    ) {
        viewModelScope.launch {
            val trimmedTitle = title.trim()
            val amount = amountText.toDoubleOrNull() ?: -1.0
            val errors = mutableListOf<String>()
            if (trimmedTitle.isEmpty()) errors.add("Title required")
            if (amount <= 0.0) errors.add("Amount must be > 0")
            if (errors.isNotEmpty()) {
                _uiState.update { it.copy(error = errors.joinToString(", ")) }
                return@launch
            }

            if (repo.getRecent(30).any {
                    it.title.equals(
                        trimmedTitle, true
                    ) && kotlin.math.abs(it.amount - amount) < 0.01 && (System.currentTimeMillis() - it.timestamp) < (30 * 60 * 1000)
                }) {
                _uiState.update { it.copy(error = "Possible duplicate detected") }
            }

            val expense = Expense(
                title = trimmedTitle,
                amount = amount,
                category = category,
                notes = notes?.take(100),
                receiptUri = receiptUri
            )
            repo.addExpense(expense)
            val list = repo.getExpensesForDate(System.currentTimeMillis())
            _uiState.update {
                it.copy(
                    expenses = list,
                    totalSpentToday = list.sumOf { e -> e.amount },
                    lastAdded = expense,
                    successMessage = "Expense added"
                )
            }
        }
    }

    fun syncNow() {
        viewModelScope.launch {
            repo.syncPending()
        }
    }

    fun clearMessages() {
        _uiState.update { it.copy(error = null, successMessage = null) }
    }

}